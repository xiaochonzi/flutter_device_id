import Flutter
import SwiftKeychainWrapper
import UIKit

public class SwiftFlutterDeviceIdPlugin: NSObject, FlutterPlugin {
    var isDefaultUseUUID = true
    let savedUniqueIdKey = "UniqueId"
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_device_id", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterDeviceIdPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      switch call.method{
      case "getPlatformVersion":
          result("iOS " + UIDevice.current.systemVersion)
          break;
      case "requestPermissions":
          result(true)
          break
      case "getUniqueId":
          result(getUniqueId())
          break
      default:
          result(nil)
      }
  }
    private func getUniqueId() -> String? {
      guard let saveUniqueId = KeychainWrapper.standard.string(forKey: savedUniqueIdKey), !saveUniqueId.isEmpty else {
        guard let uuid = UIDevice.current.identifierForVendor?.uuidString, !isDefaultUseUUID, !uuid.isEmpty else {
          let generateUUID = UUID().uuidString
          setUUIDIntoKeychain(uuid: generateUUID)
          return generateUUID
        }
        setUUIDIntoKeychain(uuid: uuid)
        return uuid
      }
      return saveUniqueId
    }
    
    private func setUUIDIntoKeychain(uuid: String?) {
        if let nonNullUUID = uuid, !nonNullUUID.isEmpty {
          KeychainWrapper.standard.set(nonNullUUID, forKey: savedUniqueIdKey)
        }
      }
}
