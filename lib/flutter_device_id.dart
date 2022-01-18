import 'dart:async';
import 'dart:io';
import 'package:flutter/services.dart';

class FlutterDeviceId {
  static const MethodChannel _channel = MethodChannel('flutter_device_id');

  static final _instance = FlutterDeviceId._();

  static FlutterDeviceId get instance => _instance;

  FlutterDeviceId._();


  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> requestPermissions() async {
    _channel.invokeMethod('requestPermissions');
  }

  Future<void> setSecretKey(String key) async {
    if (!Platform.isAndroid) return;
    if (key.length < 16) return;
    return _channel.invokeMethod('setSecretKey', key);
  }

  Future<String?> getUniqueId() => _channel.invokeMethod('getUniqueId');


}
