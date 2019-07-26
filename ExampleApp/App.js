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
  Alert,
} from 'react-native';

import React, { Component } from 'react';

const { GoogleSpeechApi } = NativeModules;

const EventEmitter = Platform.select({
  android: DeviceEventEmitter,
  ios: new NativeEventEmitter(GoogleSpeechApi),
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
  	GoogleSpeechApi.setApiKey("Your google access token")
    EventEmitter.addListener('onSpeechRecognized', (event) => {
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
    });

    EventEmitter.addListener('onSpeechRecognizedError', (error) => {
        this.setState({
          button: "Start listening"
        })
        Alert.alert(
          "Error occured", 
          error['message']
        );
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
