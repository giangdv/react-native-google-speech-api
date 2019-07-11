/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

const { GoogleSpeechApi } = NativeModules;

import React, { Component } from 'react';
import { Text, View, NativeModules } from 'react-native';

export default class App extends Component {

  componentDidMount(){
  	GoogleSpeechApi.setApiKey("Test");
    GoogleSpeechApi.start();
    GoogleSpeechApi.stop();
  }

  render() {
    return (
      <View>
        <Text>Hello world!</Text>
      </View>
    );
  }
}
