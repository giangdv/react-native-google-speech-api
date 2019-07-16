/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
  Text,
  View,
  Button,
} from 'react-native';

import React, { Component } from 'react';

const { GoogleSpeechApi } = NativeModules;

const ProperEventEmitter = Platform.select({
  android: DeviceEventEmitter,
  ios: new NativeEventEmitter(NativeModules.ModuleWithEmitter),
});

export default class App extends Component {

  constructor(props) {
    super(props);
    this.state = { 
      currentText: "", 
      previousTexts: "",
      button: "Start listening"
    };
  }

  componentDidMount(){
  	GoogleSpeechApi.setApiKey("ya29.c.ElpCB13RC3FXH8ZzSJZ7O6BibCJ2LYcqXt5P3BJgcd-gfwmFzRD181mGN2toGwaVuInF8DDahrvihaAo22Rf7OS5OHDUrmdz3vbeiAoL5ajBZKJaqGK5vgF3q9k")
    ProperEventEmitter.addListener('onSpeechRecognized', (event) => {
      var previousTexts = this.state.previousTexts;
      var currentText = event['text']
      var button = "I'm listening"
      if (event['isFinal']){
        currentText = ""
        previousTexts = event['text'] + "\n" + previousTexts;
        button = "Start listening"
      }
      this.setState({
        currentText: currentText,
        previousTexts: previousTexts,
        button: button
      });
      //GoogleSpeechApi.show(event['text'], 0)
    });
  }

  render() {
    return (
      <View style={{ margin: 30 }}>
        <Text>{this.state.currentText}</Text>
        <Text>{this.state.previousTexts}</Text>
        <Button
          title={this.state.button}
          onPress={() => {
              this.setState({
                button: "I'm listening"
              })
              GoogleSpeechApi.start()
            }
          }/>
      </View>
    );
  }
}
