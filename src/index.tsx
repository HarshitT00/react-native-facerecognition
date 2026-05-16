import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-facerecognition' doesn't seem to be linked. Make sure you rebuilt the native app package.\n`;

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
 * Recognizes a face image path against the database registry
 * @returns Promise resolving to the user's name string or "UNKNOWN"
 */
export function recognizeFace(imagePath: string): Promise<string> {
  return Facerecognition.recognizeFace(imagePath);
}

/**
 * Registers a new unique user profile with an explicit facial photo path
 * @returns Promise resolving to a confirmation status message string
 */
export function registerFace(name: string, imagePath: string): Promise<string> {
  return Facerecognition.registerFace(name, imagePath);
}