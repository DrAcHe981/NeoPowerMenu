package de.NeonSoft.neopowermenu.helpers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;
    private FingerprintInterface mInterface;

    private KeyStore keyStore;
    private static final String KEY_NAME = "example_key";
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    public FingerprintHandler(Context context) {
        appContext = context;
        generateKey();
        cipherInit();
        cryptoObject = new FingerprintManager.CryptoObject(cipher);
        addInterface(new FingerprintInterface() {
            @Override
            public void onFingerprintSuccess(FingerprintManager.AuthenticationResult result) {
                Toast.makeText(appContext,
                        "Authentication succeeded.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFingerprintFailed(int errorCode, String errorMsg) {
                Toast.makeText(appContext,
                        "Authentication error\n" + errorCode + " - " + errorMsg,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFingerprintHelp(int helpId, String helpString) {
                Toast.makeText(appContext,
                        "Authentication help\n" + helpId + " - " + helpString,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isCipherReady() {
        return cipher != null;
    }

    public void startAuth(FingerprintManager manager) {

        cancellationSignal = new CancellationSignal();

        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mInterface.onFingerprintFailed(-1, "Canceled");
            }
        });

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            mInterface.onFingerprintFailed(-1, "Missing permission");
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void stopAuth() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    public void addInterface(FingerprintInterface Interface) {
        mInterface = Interface;
    }

    public interface FingerprintInterface {
        void onFingerprintSuccess(FingerprintManager.AuthenticationResult result);

        void onFingerprintFailed(int errorCode, String errorMsg);

        void onFingerprintHelp(int helpId, String helpString);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        mInterface.onFingerprintFailed(errMsgId, errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        mInterface.onFingerprintHelp(helpMsgId, helpString.toString());
    }

    @Override
    public void onAuthenticationFailed() {
        mInterface.onFingerprintFailed(0, "");
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        mInterface.onFingerprintSuccess(result);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            mInterface.onFingerprintFailed(-1, "Failed to get KeyGenerator instance: " + e.toString());
        }

        try {
            keyStore.load(null);
            if (keyGenerator != null) {
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            } else {
                throw new RuntimeException("keyGenerator is null");
            }
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            mInterface.onFingerprintFailed(-1, "Failed generate keystore: " + e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            mInterface.onFingerprintFailed(-1, "Failed to get Cipher: " + e.toString());
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            mInterface.onFingerprintFailed(-1, "Failed to init cipher:  " + e.toString());
        }
        return false;
    }

}