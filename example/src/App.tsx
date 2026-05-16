import * as React from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import { recognizeFace } from 'react-native-facerecognition';

export default function App() {
  const [result, setResult] = React.useState<string>('Not tested yet');

  const testJavaBridge = async () => {
    try {
      setResult('Testing...');
      // We are passing a fake image path here just to see if the Java code responds.
      const res = await recognizeFace('/fake/path/to/test.jpg');
      setResult("Success: " + res);
    } catch (e: any) {
      // Because we passed a fake path, we actually EXPECT the Java code to throw an "IMAGE_ERROR" here.
      // If we see that specific error, it proves the JS -> Java bridge is working perfectly!
      if (e.code === "IMAGE_ERROR") {
        setResult("✅ Bridge Works! Java responded: " + e.message);
      } else {
        setResult("❌ Error: " + e.message);
      }
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Face Recognition Library Test</Text>
      <Text style={styles.resultText}>{result}</Text>
      
      <Button title="Test Bridge" onPress={testJavaBridge} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  text: {
    fontSize: 20,
    marginBottom: 20,
  },
  resultText: {
    fontSize: 16,
    marginVertical: 20,
    textAlign: 'center',
    color: 'blue',
  }
});