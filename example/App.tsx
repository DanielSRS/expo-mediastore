import { StyleSheet, Text, View } from 'react-native';

import * as ExpoMediastore from 'expo-mediastore';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ExpoMediastore.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
