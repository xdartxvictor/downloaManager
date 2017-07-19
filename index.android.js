/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import DownloadManagerCustom from './downloadmanager';
import { DeviceEventEmitter } from 'react-native';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  Alert
} from 'react-native';

export default class DownloadManager extends Component {
  componentWillMount () {
    DeviceEventEmitter.addListener('downloadCompleate', function(e: Event) {
      // handle event.
      Alert.alert('Download Compleate');
    });
  }

  onLoginClick() {
    DownloadManagerCustom.download(
      (loginData) => { Alert.alert(`Welcome `);}
      //loginData) => { Alert.alert(`Welcome ${loginData.userName}, access token: ${loginData.authToken}, secret access token: ${loginData.authTokenSecret}`);}
    )
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
        <TouchableHighlight onPress={this.onLoginClick.bind(this)}>
          <View style={styles.button}>
            <Text style={styles.buttonText}>callback android</Text>
          </View>
        </TouchableHighlight>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('DownloadManager', () => DownloadManager);
