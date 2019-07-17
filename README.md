
# react-native-google-speech-api

## Getting started

`$ npm install react-native-google-speech-api --save`

### Mostly automatic installation

`$ react-native link react-native-google-speech-api`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-google-speech-api` and add `RNGoogleSpeechApi.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNGoogleSpeechApi.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.GoogleSpeechApiPackage;` to the imports at the top of the file
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-google-speech-api'
    project(':react-native-google-speech-api').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-google-speech-api/android')
  	```
3. Insert the following line inside the `dependencies` block in `android/app/build.gradle`:
  	```
      implementation project(':react-native-google-speech-api')
  	```
### After installation


#### iOS


#### Android
1. Insert following line inside `dependencies` block in `android/app/build.gradle`:
    ```
      implementation 'androidx.multidex:multidex:2.0.1'
    ```
2. Insert following line inside `defaultConfig` block in `android/app/build.gradle`:
    ```
      multiDexEnabled true
    ```
3. Insert following line inside `<manifest>` tag in `android/app/src/main/AndroidManifest.xml`:
    ```
      <uses-permission android:name="android.permission.RECORD_AUDIO" />
    ```
Hint: from Android API 23 (6.0) you should request RECORD_AUDIO permission at runtime. For test purposes you can turn on the permission from device settings (For more info see `Turn permissions on or off` section [here](https://support.google.com/googleplay/answer/6270602?hl=en))

## Usage
```javascript
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

const EventEmitter = Platform.select({
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
```
  
