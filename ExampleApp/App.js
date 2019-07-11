/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

const { GoogleSpeechApi } = NativeModules;

import React, { Component } from 'react';
import { Text, View, NativeModules, Button } from 'react-native';

export default class App extends Component {

  componentDidMount(){
  	GoogleSpeechApi.setApiKey("Test");
  }

  render() {
    return (
      <View>
        <Button
          title={"start voice recorder"}
          onPress={() => {
              GoogleSpeechApi.start()
            }
          }/>
        <Button
          title={"stop voice recorder"}
          onPress={() => {
              GoogleSpeechApi.stop()
            }
          }/>
      </View>
    );
  }
}
