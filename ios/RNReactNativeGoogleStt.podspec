
Pod::Spec.new do |s|
  s.name         = "RNReactNativeGoogleStt"
  s.version      = "0.0.1"
  s.summary      = "RNReactNativeGoogleStt"
  s.description  = <<-DESC
                  RNReactNativeGoogleStt
                   DESC
  s.homepage     = "https://none.com"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeGoogleStt.git", :tag => "master" }
  s.source_files  = "RNReactNativeGoogleStt/ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency 'React'
  # Run protoc with the Objective-C and gRPC plugins to generate protocol messages and gRPC clients.
  s.dependency "!ProtoCompiler-gRPCPlugin", "~> 1.0"

  # Pods directory corresponding to this app's Podfile, relative to the location of this podspec.
  pods_root = '../../../ios/Pods'

  # Path where Cocoapods downloads protoc and the gRPC plugin.
  protoc_dir = "#{pods_root}/!ProtoCompiler"
  protoc = "#{protoc_dir}/protoc"
  plugin = "#{pods_root}/!ProtoCompiler-gRPCPlugin/grpc_objective_c_plugin"

  # Run protoc with the Objective-C and gRPC plugins to generate protocol messages and gRPC clients.
  # You can run this command manually if you later change your protos and need to regenerate.  
  s.prepare_command = <<-CMD
    #{protoc} \
        --plugin=protoc-gen-grpc=#{plugin} \
        --objc_out=. \
        --grpc_out=. \
        -I . \
        -I #{protoc_dir} \
        google/*/*.proto google/*/*/*/*.proto
  CMD

  # The --objc_out plugin generates a pair of .pbobjc.h/.pbobjc.m files for each .proto file.
  s.subspec "Messages" do |ms|
    ms.source_files = "google/**/*.pbobjc.{h,m}"
    ms.header_mappings_dir = "."
    ms.requires_arc = false
    ms.dependency "Protobuf"
  end

  # The --objcgrpc_out plugin generates a pair of .pbrpc.h/.pbrpc.m files for each .proto file with
  # a service defined.
  s.subspec "Services" do |sss|
    sss.source_files = "google/**/*.pbrpc.{h,m}"
    sss.header_mappings_dir = "."
    sss.requires_arc = true
    sss.dependency "gRPC-ProtoRPC"
    sss.dependency "#{s.name}/Messages"
  end
  
  s.pod_target_xcconfig = {
    'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS=1',
    'USER_HEADER_SEARCH_PATHS' => '$SRCROOT/..'
  }
end

  