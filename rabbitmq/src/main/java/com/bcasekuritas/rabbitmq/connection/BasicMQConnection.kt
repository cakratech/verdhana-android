package com.bcasekuritas.rabbitmq.connection

import com.rabbitmq.client.Address
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Recoverable
import com.rabbitmq.client.RecoveryDelayHandler.DefaultRecoveryDelayHandler
import com.rabbitmq.client.RecoveryListener
import org.bouncycastle.jce.provider.BouncyCastleProvider
import kotlin.concurrent.Volatile

import timber.log.Timber
import java.io.IOException
import java.security.Security
import java.util.*
import javax.net.ssl.SSLContext

class BasicMQConnection {

    // Connection properties
    var dataPayload: String? = null
    var sequenceBuffer: String? = null
    var virtualHost: String? = null
    var uri: String? = null
    var isUseSSL: Boolean = false
    var isSelfSignCertificate: Boolean = false
    var sslContext: SSLContext? = null
    var isAutomaticRecoveryEnabled: Boolean = true
    var requestedHeartbeat: Int = 0
    var handshakeTimeout: Int = 0
    var connectionTimeout: Int = 0
    var shutdownTimeout: Int = 0
    var port: Int = 5672

    // SSL properties
    var versionSSL: String? = null
    var keystore: String? = null
    var passphrase: String? = null

    // Connection management
    @Volatile
    var connection: Connection? = null
    protected var closed: Boolean = false
    private var connectionListener: IMQConnectionListener? = null

    // Builder-style methods
    fun connectionListener(listener: IMQConnectionListener) = apply {
        this.connectionListener = listener
    }

    fun automaticRecoveryEnabled(enabled: Boolean) = apply { isAutomaticRecoveryEnabled = enabled }
    fun requestedHeartbeat(heartbeat: Int) = apply { requestedHeartbeat = heartbeat }
    fun handshakeTimeout(handshake: Int) = apply { handshakeTimeout = handshake }
    fun connectionTimeout(connection: Int) = apply { connectionTimeout = connection }
    fun shutdownTimeout(shutdown: Int) = apply { shutdownTimeout = shutdown }
    fun dataPayload(dataPayload: String?) = apply { this.dataPayload = dataPayload }
    fun sequenceBuffer(sequenceBuffer: String?) = apply { this.sequenceBuffer = sequenceBuffer }
    fun virtualHost(virtualHost: String?) = apply { this.virtualHost = virtualHost }
    fun uri(uri: String) = apply { this.uri = uri }
    fun useSsl(useSSL: Boolean) = apply { isUseSSL = useSSL }
    fun sslContext(context: SSLContext?) = apply { sslContext = context }
    fun selfSignCertificate(selfSign: Boolean) = apply { isSelfSignCertificate = selfSign }
    fun port(port: Int) = apply { this.port = port }

    @Synchronized
    @Throws(Exception::class)
    fun connect() {
        if (connection?.isOpen == true) {
            Timber.d("Connection already established")
            return
        }

        try {
            createConnection()
            Timber.d("Connection established successfully")
        } catch (e: Exception) {
            Timber.e(e, "Connection failed")
            throw e
        }
    }

    fun isConnected(): Boolean = connection?.isOpen ?: false

    @Throws(Exception::class)
    fun getConnectionMQ(): Connection? {
        if (connection == null || !connection!!.isOpen) {
            connect()
        }
        return connection
    }

    @Synchronized
    @Throws(Exception::class)
    protected fun createConnection(): Connection? {
        check(!closed) { "Connection is closed" }
        if (connection?.isOpen == true) return connection

        Security.addProvider(BouncyCastleProvider())
        val factory = createConnectionFactory()

        connection = if (isUseSSL) {
            createSslConnection(factory)
        } else {
            createRegularConnection(factory)
        }

        setupConnectionListeners()
        return connection
    }

    private fun createConnectionFactory(): ConnectionFactory {
        return ConnectionFactory().apply {
            username = dataPayload
            password = this@BasicMQConnection.sequenceBuffer
            isAutomaticRecoveryEnabled = this@BasicMQConnection.isAutomaticRecoveryEnabled
            recoveryDelayHandler = DefaultRecoveryDelayHandler(500L)
            requestedHeartbeat = this@BasicMQConnection.requestedHeartbeat
            handshakeTimeout = this@BasicMQConnection.handshakeTimeout
            connectionTimeout = this@BasicMQConnection.connectionTimeout
            shutdownTimeout = this@BasicMQConnection.shutdownTimeout
            virtualHost = this@BasicMQConnection.virtualHost
            port = this@BasicMQConnection.port

            if (isSelfSignCertificate) {
                useSslProtocol()
            }
        }
    }

    private fun createSslConnection(factory: ConnectionFactory): Connection {
        factory.host = uri
        sslContext?.let { factory.useSslProtocol(it) }
        return factory.newConnection()
    }

    private fun createRegularConnection(factory: ConnectionFactory): Connection {
        return uri?.split(",")?.map { Address(it.trim()) }?.toTypedArray()?.let { addresses ->
            if (addresses.size > 1) {
                factory.newConnection(addresses)
            } else {
                factory.host = uri
                factory.newConnection()
            }
        } ?: factory.newConnection()
    }

    private fun setupConnectionListeners() {
        (connection as? Recoverable)?.addRecoveryListener(object : RecoveryListener {
            override fun handleRecovery(recoverable: Recoverable?) {
                connectionListener?.onListener("Done Recovered")
                connectionListener?.countLatch()
                Timber.i("Connection recovered: $recoverable")
            }

            override fun handleRecoveryStarted(recoverable: Recoverable?) {
                connectionListener?.onListener("Recovery")
                Timber.i("Connection recovery started: $recoverable")
            }
        })

        connection?.addShutdownListener { cause ->
            connectionListener?.onListener("Shutdown")
            connectionListener?.doLatch()
            Timber.e("Connection shutdown: $cause")
        }
    }

    @Synchronized
    @Throws(IOException::class)
    fun close() {
        closed = true
        connection?.close()
    }
}

