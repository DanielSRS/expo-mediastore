import { Button, PermissionsAndroid, Platform, StyleSheet, ToastAndroid, View } from 'react-native';

import * as ExpoMediastore from 'expo-mediastore';

export const hasMusicPermission = async () => {
  if (Platform.OS === 'ios') {
    return false;
  }

  let permission = PermissionsAndroid.PERMISSIONS.READ_MEDIA_AUDIO;
  console.log('platform verison: ', Platform.Version);
  console.log('permission: ', permission);
  if (Platform.Version < '30' || permission === undefined) {
    permission = PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE;
  }

  const hasPermission = await PermissionsAndroid.check(permission);

  if (hasPermission) {
    return true;
  }

  const status = await PermissionsAndroid.request(permission);

  if (status === PermissionsAndroid.RESULTS.GRANTED) {
    return true;
  }

  if (status === PermissionsAndroid.RESULTS.DENIED) {
    ToastAndroid.show('Media audio permission denied by user.', ToastAndroid.LONG);
  } else if (status === PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN) {
    ToastAndroid.show('Media audio permission revoked by user.', ToastAndroid.LONG);
  }

  return false;
};

export default function App() {
  return (
    <View style={styles.container}>
      <Button title={'get musics'} onPress={() => {
        const val = ExpoMediastore.getMusicMedias();
        console.debug('get object response: ',JSON.stringify(val,null, 2));
      }} />
      <Button title={'get albums'} onPress={() => {
        const val = ExpoMediastore.getAlbums();
        console.debug('getAlbums response: ',JSON.stringify(val,null, 2));
      }} />
      <Button title={'Get permission'} onPress={async () => {
        const hasPermission = await hasMusicPermission();
        if (!hasPermission) {
          console.error('There is no permission');
          return;
        }
        console.log('has permission');
      }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
  },
});
