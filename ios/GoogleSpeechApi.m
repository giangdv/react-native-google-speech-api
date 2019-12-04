#import <AVFoundation/AVFoundation.h>
#import "google/cloud/speech/v1/CloudSpeech.pbrpc.h"
#import "AudioController.h"
#import "SpeechRecognitionService.h"

#import "GoogleSpeechApi.h"

#define SAMPLE_RATE 16000.0f

@interface GoogleSpeechApi () <AVAudioRecorderDelegate, AVAudioPlayerDelegate, AudioControllerDelegate>
@property (strong, nonatomic) AVAudioRecorder *audioRecorder;
@property (strong, nonatomic) AVAudioSession *audioSession;
@property (strong, nonatomic) NSString *apiKey;
@property (strong, nonatomic) NSString *languageCode;
@property (nonatomic, strong) NSMutableData *audioData;
@end

@implementation GoogleSpeechApi

RCT_EXPORT_MODULE()

#pragma mark - EXPORT METHODS

RCT_EXPORT_METHOD(setApiKey:(NSString *)apiKey) {
    _apiKey = apiKey;
}

RCT_EXPORT_METHOD(init:(NSString *)apiKey  languageCode:(NSString *)languageCode) {
    _apiKey = apiKey;
    _languageCode = languageCode;
}

RCT_EXPORT_METHOD(start) {
    [AudioController sharedInstance].delegate = self;
    _audioSession = [AVAudioSession sharedInstance];
    [_audioSession setCategory:AVAudioSessionCategoryRecord error:nil];
    
    _audioData = [[NSMutableData alloc] init];
    [[AudioController sharedInstance] prepareWithSampleRate:SAMPLE_RATE];
    [[SpeechRecognitionService sharedInstance] setSampleRate:SAMPLE_RATE];
    [[SpeechRecognitionService sharedInstance] setApiKey:_apiKey];
    [[SpeechRecognitionService sharedInstance] setLanguageCode:_languageCode];
    [[AudioController sharedInstance] start];
}

RCT_EXPORT_METHOD(stop) {
    [self stopSpeech];
}

#pragma mark -

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

- (NSArray<NSString *>*)supportedEvents {
    return @[@"onSpeechRecognized", @"onSpeechRecognizedError"];
}

- (NSString *)soundFilePath {
    NSArray *dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *docsDir = dirPaths[0];
    return [docsDir stringByAppendingPathComponent:@"sound.caf"];
}

- (void)stopSpeech {
    [[AudioController sharedInstance] stop];
    [[SpeechRecognitionService sharedInstance] stopStreaming];
    [_audioSession setCategory:AVAudioSessionCategoryPlayback error:nil];
}

- (void)processSampleData:(NSData *)data {
    [self.audioData appendData:data];
    NSInteger frameCount = [data length] / 2;
    int16_t *samples = (int16_t *) [data bytes];
    int64_t sum = 0;
    for (int i = 0; i < frameCount; i++) {
        sum += abs(samples[i]);
    }
    
    // We recommend sending samples in 100ms chunks
    int chunk_size = 0.1 /* seconds/chunk */ * SAMPLE_RATE * 2 /* bytes/sample */ ; /* bytes/chunk */
    
    if ([self.audioData length] > chunk_size) {
        [[SpeechRecognitionService sharedInstance] streamAudioData:self.audioData
                                                    withCompletion:^(StreamingRecognizeResponse *response, NSError *error) {
                                                        if (error) {
                                                            [self sendEventWithName:@"onSpeechRecognizedError" body:@{@"message": [error localizedDescription], @"isFinal":@(YES)}];
                                                            [self stopSpeech];
                                                        } else if (response) {
                                                            BOOL finished = NO;
                                                            for (StreamingRecognitionResult *result in response.resultsArray) {
                                                                if (result.isFinal) {
                                                                    finished = YES;
                                                                }
                                                            }
                                                            [self sendEventWithName:@"onSpeechRecognized" body:@{@"text": response.resultsArray.firstObject.alternativesArray.firstObject.transcript, @"isFinal":@(finished)}];
                                                            if (finished) {
                                                                [self stopSpeech];
                                                            }
                                                        }
                                                    }
         ];
        self.audioData = [[NSMutableData alloc] init];
    }
}

@end
