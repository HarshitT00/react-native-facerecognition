package com.facerecognition;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

// Import your copied ML classes
import org.tensorflow.lite.examples.detection.tflite.SimilarityClassifier;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;

public class FacerecognitionModule extends ReactContextBaseJavaModule {
    public static final String NAME = "Facerecognition";
    private SimilarityClassifier classifier;

    public FacerecognitionModule(ReactApplicationContext reactContext) {
        super(reactContext);
        
        // Initialize the TFLite model when the app starts
        try {
            // Note: 112 is the standard input size for MobileFaceNet. 
            // If your specific model uses 160 or something else, you can change it here.
            classifier = TFLiteObjectDetectionAPIModel.create(
                reactContext.getAssets(),
                "mobile_face_net.tflite",
                "labelmap.txt",
                112, 
                false // isQuantized
            );
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    // @ReactMethod exposes this function to your JavaScript/React Native code
    @ReactMethod
    public void recognizeFace(String imagePath, Promise promise) {
        if (classifier == null) {
            promise.reject("MODEL_ERROR", "Face recognition model failed to initialize.");
            return;
        }

        try {
            // 1. Clean the file path and decode it into an Android Bitmap
            String cleanPath = imagePath.replace("file://", "");
            Bitmap bitmap = BitmapFactory.decodeFile(cleanPath);

            if (bitmap == null) {
                promise.reject("IMAGE_ERROR", "Could not read the image at: " + cleanPath);
                return;
            }

            // 2. Here you will pass the bitmap to your classifier. 
            // (Leaving it commented out until we confirm it compiles, just to be safe!)
            // final List<SimilarityClassifier.Recognition> results = classifier.recognizeImage(bitmap, true);
            
            // 3. Return a success message to JS for our initial test
            promise.resolve("Success! Model is loaded and image was received.");

        } catch (Exception e) {
            promise.reject("FACE_RECOGNITION_ERROR", e.getMessage());
        }
    }
}