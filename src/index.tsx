import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-facerecognition' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// Grab our Java module
const Facerecognition = NativeModules.Facerecognition
  ? NativeModules.Facerecognition
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * Pass a local file path (e.g., from react-native-vision-camera or image-picker)
 * to the native Android face recognition model.
 * * @param imagePath The absolute local path to the image file (e.g., file:///storage/...)
 * @returns A promise that resolves with the recognition result string.
 */
export function recognizeFace(imagePath: string): Promise<string> {
  return Facerecognition.recognizeFace(imagePath);
}