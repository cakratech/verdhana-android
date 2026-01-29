#include <jni.h>
#include <string>

using namespace std;

// VARIANT DEV
const int ENV_VARIANT_BCA = 0;

// ENVIRONMENT
const int ENV_PRODUCTION = 1;
const int ENV_STAGING = 2;
const int ENV_DEVELOPMENT = 3;

// environment development
const string DEV_SALT_KEY = "SECRET";
const string DEV_PREFERENCE_NAME = "bca-app-dev.xml";

// BCA
//http://103.165.135.254:8086/3/D/RG/BBCA/ali/MACD
//const string DEV_BCA_END_POINT = "192.168.14.109";
//const string DEV_BCA_PORT = "5672";
const string DEV_BCA_END_POINT = "appoltv2.dgiok.net";
const string DEV_BCA_END_POINT_DRC = "103.165.135.228";
const string DEV_BCA_PORT = "5671";
const string DEV_BCA_VHOST = "/";
const string DEV_BCA_TRADING_VIEW_URL = "https://chart.bcasekuritas.co.id/";
const string DEV_BCA_FIREBASE_PROJECT_ID = "percobaan-67fdd";
const string DEV_BCA_FIREBASE_APP_ID = "1:222953620899:android:76b21cc7e4530bab920616";
const string DEV_BCA_FIREBASE_API_KEY = "AIzaSyC34YfGJI26bHHcAXSs1-jkBWVPh0r5Ghk";
const string DEV_BCA_FORGOT_PASS_URL = "http://sq-dev1.cnaindo.com:51080/account/home";
const string DEV_BCA_PRE_LOGIN_URL = "https://account.bcasekuritas.co.id/home";
const string DEV_BCA_PORT_BANNER = "443";

// environment staging
const string STAGING_END_POINT = "https://";
const string STAGING_END_POINT_DRC = "https://";
const string STAGING_PORT = "5671";
const string STAGING_VHOST = "retail";
const string STAGING_TRADING_VIEW_URL = "https://chart.bcasekuritas.co.id/";
const string STAGING_FIREBASE_PROJECT_ID = "percobaan-67fdd";
const string STAGING_FIREBASE_APP_ID = "";
const string STAGING_FIREBASE_API_KEY = "";
const string STAGING_GOOGLE_SERVER_AUTH = "";
const string STAGING_FORGOT_PASS_URL = "http://sq-dev1.cnaindo.com:51080/account/home";
const string STAGING_PRE_LOGIN_URL = "https://fpre.bcasekuritas.co.id/bcas/";
const string STAGING_PORT_BANNER = "8080";

const string STAGING_SALT_KEY = "SECRET";
const string STAGING_PREFERENCE_NAME = "bca-rabbit-stg.xml";


// environment production
//const string PROD_END_POINT = "103.165.135.253";
const string PROD_END_POINT = "mtsv2.bcasekuritas.co.id";
const string PROD_END_POINT_DRC = "103.165.135.228";
const string PROD_PORT = "5671";
const string PROD_VHOST = "/";
const string PROD_TRADING_VIEW_URL = "https://chart.bcasekuritas.co.id/";
const string PROD_FIREBASE_PROJECT_ID = "percobaan-67fdd";
const string PROD_FIREBASE_APP_ID = "";
const string PROD_FIREBASE_API_KEY = "";
const string PROD_GOOGLE_SERVER_AUTH = "";
const string PROD_FORGOT_PASS_URL = "https://mobile.bcasekuritas.co.id/account";
const string PROD_PRE_LOGIN_URL = "https://account.bcasekuritas.co.id/home";
const string PROD_BCA_PDF_URL = "https://bcasekuritas.co.id/api/pdf/Formulir%20Pengkinian%20Data%20Perorangan.pdf";
const string PROD_BCA_PORT_BANNER = "8080";

const string PROD_SALT_KEY = "SECRET";
const string PROD_PREFERENCE_NAME = "bca-app.xml";


unsigned char metric_data_1[] = {
        0x36, 0x39, 0x33, 0x25, 0x38, 0x3e, 0x33
};

unsigned char metric_data_2[] = {
        0x16, 0x39, 0x33, 0x25, 0x38, 0x3e, 0x33, 0x66, 0x65, 0x64, 0x73
};

unsigned char metric_data_3[] = {
        0x2f, 0x33, 0x0f, 0x61, 0x1e, 0x2f, 0x07, 0x63, 0x16, 0x30,
        0x26, 0x60, 0x3a, 0x0e, 0x23, 0x26
};

unsigned char metric_data_4[] = {
        0x07, 0x1b, 0x62, 0x23, 0x31, 0x39, 0x27, 0x65, 0x72,
        0x74, 0x16, 0x25, 0x60, 0x0d, 0x30, 0x02, 0x7e, 0x64, 0x24, 0x71, 0x3d, 0x2d, 0x22,
        0x1a, 0x2f, 0x09, 0x73, 0x32, 0x12, 0x36, 0x11, 0x1f, 0x63, 0x21,
        0x20, 0x26, 0x05, 0x0e, 0x7c, 0x0f
};


// Fungsi dekripsi dengan nama samaran
std::string transformData(unsigned char* data, int len) {
    std::string out = "";
    for (int i = 0; i < len; i++) {
        out += (char)(data[i] ^ BUFFER_SIZE);
    }
    return out;
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getSaltKey(
        JNIEnv *env, jobject thiz, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_SALT_KEY;
    } else if (type == ENV_STAGING) {
        s = STAGING_SALT_KEY;
    } else {
        s = DEV_SALT_KEY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getPreferenceName(
        JNIEnv *env, jobject thiz, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_PREFERENCE_NAME;
    } else if (type == ENV_STAGING) {
        s = STAGING_PREFERENCE_NAME;
    } else {
        s = DEV_PREFERENCE_NAME;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getEndPoint(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_END_POINT;
    } else if (type == ENV_STAGING) {
        s = STAGING_END_POINT;
    } else {
        s = DEV_BCA_END_POINT;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getEndPointDrc(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_END_POINT_DRC;
    } else if (type == ENV_STAGING) {
        s = STAGING_END_POINT_DRC;
    } else {
        s = DEV_BCA_END_POINT;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getPort(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_PORT;
    } else if (type == ENV_STAGING) {
        s = STAGING_PORT;
    } else {
        s = DEV_BCA_PORT;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getVHost(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_VHOST;
    } else if (type == ENV_STAGING) {
        s = STAGING_VHOST;
    } else {
        s = DEV_BCA_VHOST;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getDataMetric(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    std::string s;

    if (type == ENV_PRODUCTION) {
        s = transformData(metric_data_1, sizeof(metric_data_1));
    } else if (type == ENV_STAGING) {
        s = transformData(metric_data_1, 9);;
    } else {
        s = transformData(metric_data_1, sizeof(metric_data_1));
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getMutipleMetric(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    std::string s;

    if (type == ENV_PRODUCTION) {
        s = transformData(metric_data_2, sizeof(metric_data_2));
    } else if (type == ENV_STAGING) {
        s = transformData(metric_data_2, 9);
    } else {
        s = transformData(metric_data_2, sizeof(metric_data_2));
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getTradingViewUrl(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_TRADING_VIEW_URL;
    } else if (type == ENV_STAGING) {
        s = STAGING_TRADING_VIEW_URL;
    } else {
        s = DEV_BCA_TRADING_VIEW_URL;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getForgotPassUrl(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_FORGOT_PASS_URL;
    } else if (type == ENV_STAGING) {
        s = STAGING_FORGOT_PASS_URL;
    } else {
        s = DEV_BCA_FORGOT_PASS_URL;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getPreLoginUrl(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_PRE_LOGIN_URL;
    } else if (type == ENV_STAGING) {
        s = STAGING_PRE_LOGIN_URL;
    } else {
        s = DEV_BCA_PRE_LOGIN_URL;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getPdfUrl(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_BCA_PDF_URL;
    } else if (type == ENV_STAGING) {
        s = PROD_BCA_PDF_URL;
    } else {
        if (variant == ENV_VARIANT_BCA) {
            s = PROD_BCA_PDF_URL;
        } else {
            s = PROD_BCA_PDF_URL;
        }
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_mybest_ext_constant_ConstKeys_getPromoBannerUrl(
        JNIEnv *env, jobject thiz, jint envtype, jint envvariant) {
    int type = (int) envtype;
    int variant = (int) envvariant;
    string s;

    if (type == ENV_PRODUCTION) {
        s = "https://" + PROD_END_POINT;
    } else if (type == ENV_STAGING) {
        s = "http://" + STAGING_END_POINT+ ":" + STAGING_PORT_BANNER;
    } else {
        s = "https://" + DEV_BCA_END_POINT;
    }

    return env->NewStringUTF(s.c_str());
}

//extern "C" jstring JNICALL
//Java_id_co_kalacakra_rabbitmq_constant_ConstantKeys_getSaltKey(
//        JNIEnv *env, jclass instance, jint envtype) {
//    int type = (int) envtype;
//    string s;
//
//    if (type == ENV_PRODUCTION) {
//        s = PROD_SALT_KEY;
//    } else {
//        s = DEV_SALT_KEY;
//    }
//
//    return env->NewStringUTF(s.c_str());
//}
//
//extern "C" jstring JNICALL
//Java_id_co_kalacakra_rabbitmq_constant_ConstantKeys_getPreferenceName(
//        JNIEnv *env, jclass instance, jint envtype) {
//    int type = (int) envtype;
//    string s;
//
//    if (type == ENV_PRODUCTION) {
//        s = PROD_PREFERENCE_NAME;
//    } else {
//        s = DEV_PREFERENCE_NAME;
//    }
//
//    return env->NewStringUTF(s.c_str());
//}