package com.facerecognition;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.tensorflow.lite.examples.detection.tflite.SimilarityClassifier;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.util.List;

public class FacerecognitionModule extends ReactContextBaseJavaModule {
    public static final String NAME = "Facerecognition";
    private SimilarityClassifier classifier;
    private static final float MATCH_THRESHOLD = 1.0f; // Lower distance means tighter match accuracy

    public FacerecognitionModule(ReactApplicationContext reactContext) {
        super(reactContext);
        try {
            classifier = TFLiteObjectDetectionAPIModel.create(
                reactContext.getAssets(),
                "mobile_face_net.tflite",
                "labelmap.txt",
                112, 
                false
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

    // 1. PERFORM LIVE FACE RECOGNITION MATCH
    @ReactMethod
    public void recognizeFace(String imagePath, Promise promise) {
        if (classifier == null) {
            promise.reject("MODEL_ERROR", "Face recognition model failed to initialize.");
            return;
        }

        try {
            String cleanPath = imagePath.replace("file://", "");
            Bitmap bitmap = BitmapFactory.decodeFile(cleanPath);

            if (bitmap == null) {
                promise.reject("IMAGE_ERROR", "Could not read the image at: " + cleanPath);
                return;
            }

            // Run the TFLite inference to find nearest matching face embedding
            final List<SimilarityClassifier.Recognition> results = classifier.recognizeImage(bitmap, false);
            
            if (results != null && !results.isEmpty()) {
                SimilarityClassifier.Recognition match = results.get(0);
                String matchedLabel = match.getTitle(); // Registered name or "?"
                float distance = match.getDistance();

                if (!matchedLabel.equals("?") && distance < MATCH_THRESHOLD) {
                    promise.resolve(matchedLabel); // Successfully matched individual
                } else {
                    promise.resolve("UNKNOWN");
                }
            } else {
                promise.resolve("UNKNOWN");
            }

        } catch (Exception e) {
            promise.reject("FACE_RECOGNITION_ERROR", e.getMessage());
        }
    }

    // 2. REGISTER A NEW PLAYER/COACH FACE EMBEDDING
    @ReactMethod
    public void registerFace(String name, String imagePath, Promise promise) {
        if (classifier == null) {
            promise.reject("MODEL_ERROR", "Face recognition model failed to initialize.");
            return;
        }

        try {
            String cleanPath = imagePath.replace("file://", "");
            Bitmap bitmap = BitmapFactory.decodeFile(cleanPath);

            if (bitmap == null) {
                promise.reject("IMAGE_ERROR", "Could not read the image at: " + cleanPath);
                return;
            }

            // Generate face embeddings vector (storeExtra = true maps embeddings into 'extra' field)
            final List<SimilarityClassifier.Recognition> results = classifier.recognizeImage(bitmap, true);
            
            if (results != null && !results.isEmpty()) {
                SimilarityClassifier.Recognition rawRecognitionObject = results.get(0);
                
                // Save name label and vector array into the Classifier registry map
                classifier.register(name, rawRecognitionObject);
                promise.resolve("Successfully registered face features for: " + name);
            } else {
                promise.reject("REGISTRATION_ERROR", "Could not process facial features from the image.");
            }

        } catch (Exception e) {
            promise.reject("REGISTRATION_ERROR", e.getMessage());
        }
    }
}