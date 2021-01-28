
package com.reactlibrarynativeprinter;

import android.content.Context;
import android.widget.Toast;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.printer.StatusChangeListener;
import com.facebook.react.bridge.ObjectAlreadyConsumedException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNNativeEscPrinterLibraryModule extends ReactContextBaseJavaModule implements StatusChangeListener {

  private final ReactApplicationContext reactContext;
  Printer printer = null;
  private static final String ON_DISCOVER_PRINTER = "onDiscoverPrinter";

  public RNNativeEscPrinterLibraryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNNativeEscPrinterLibrary";
  }

  @ReactMethod
  public void show(String text) {
    Context context = getReactApplicationContext();
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }

  @ReactMethod
  public void display(){
    Context context = getReactApplicationContext();
    Toast.makeText(context, "Display method", Toast.LENGTH_LONG).show();
  }

  //Discover Printer
  @ReactMethod
  public void StartDiscoverPrinter() {
    FilterOption mFilterOption = new FilterOption();
    mFilterOption.setPortType(Discovery.PORTTYPE_ALL);
    mFilterOption.setDeviceType(Discovery.TYPE_ALL);
    mFilterOption.setDeviceModel(Discovery.MODEL_ALL);
    mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
    mFilterOption.setBroadcast("255.255.255.255");
    try {
      Discovery.start(reactContext, mFilterOption, mDiscoveryListener);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
    @Override
    public void onDiscovery(final DeviceInfo deviceInfo) {
      try {
        WritableMap map = new WritableNativeMap();
        map.putString("PrinterName", deviceInfo.getDeviceName());
        map.putString("Target", deviceInfo.getTarget());
        map.putString("bdAddress", deviceInfo.getBdAddress());
        map.putString("ipAddress", deviceInfo.getIpAddress());
        System.out.println("on get device"+ deviceInfo.getDeviceType());
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(ON_DISCOVER_PRINTER, map);
      } catch (ObjectAlreadyConsumedException e) {
        System.out.println(e);
      }
    }
  };

  @ReactMethod
  public void stopDiscovery() {
    try {
      Discovery.stop();
    } catch (Epos2Exception e) {
      System.out.println(e);
    }
  }
  @ReactMethod
  public Boolean ConnectPrinter(String TargetIP, String DeviceName) {
    if (TargetIP != null) {
      int PrinterName = 0;
      if (DeviceName.contains("m10")) {
        PrinterName = Printer.TM_M10;
      } else if (DeviceName.contains("m30")) {
        PrinterName = Printer.TM_M30;
      } else if (DeviceName.contains("p20")) {
        PrinterName = Printer.TM_P20;
      } else if (DeviceName.contains("p60")) {
        PrinterName = Printer.TM_P60;
      } else if (DeviceName.contains("p60II")) {
        PrinterName = Printer.TM_P60II;
      } else if (DeviceName.contains("p80")) {
        PrinterName = Printer.TM_P80;
      } else if (DeviceName.contains("t20")) {
        PrinterName = Printer.TM_T20;
      } else if (DeviceName.contains("t60")) {
        PrinterName = Printer.TM_T60;
      } else if (DeviceName.contains("t70")) {
        PrinterName = Printer.TM_T70;
      } else if (DeviceName.contains("t81")) {
        PrinterName = Printer.TM_T81;
      } else if (DeviceName.contains("t82")) {
        PrinterName = Printer.TM_T82;
      } else if (DeviceName.contains("t83")) {
        PrinterName = Printer.TM_T83;
      } else if (DeviceName.contains("t88")) {
        PrinterName = Printer.TM_T88;
      } else if (DeviceName.contains("t90")) {
        PrinterName = Printer.TM_T90;
      } else if (DeviceName.contains("t90KP")) {
        PrinterName = Printer.TM_T90KP;
      } else if (DeviceName.contains("u220")) {
        PrinterName = Printer.TM_U220;
      } else if (DeviceName.contains("u330")) {
        PrinterName = Printer.TM_U330;
      } else if (DeviceName.contains("l90")) {
        PrinterName = Printer.TM_L90;
      } else if (DeviceName.contains("h6000")) {
        PrinterName = Printer.TM_H6000;
      } else if (DeviceName.contains("t83III")) {
        PrinterName = Printer.TM_T83III;
      } else if (DeviceName.contains("t100")) {
        PrinterName = Printer.TM_T100;
      } else if (DeviceName.contains("m30II")) {
        PrinterName = Printer.TM_M30II;
      } else if (DeviceName.contains("ts_100")) {
        PrinterName = Printer.TS_100;
      } else if (DeviceName.contains("m50")) {
        PrinterName = Printer.TM_M50;
      }
      try {
        printer = new Printer(PrinterName, Printer.MODEL_ANK, reactContext);
        printer.setStatusChangeEventListener(this);
        printer.setReceiveEventListener(new ReceiveListener() {
          @Override
          public void onPtrReceive(Printer printer, int code, PrinterStatusInfo printerStatusInfo, String s) {
            dispPrinterWarnings(printerStatusInfo,s,code);
          }
        });
        return ConnectedPrinter(TargetIP);
      } catch (Epos2Exception e) {
        return false;
      }
    }
    return false;
  }
  @ReactMethod
  public void disconnectPrinter(){
    try { printer.disconnect();
      printer.clearCommandBuffer();
      printer.setReceiveEventListener(null);
    }
    catch (Epos2Exception e) {
//Displays error messages
    }
  }
  private void dispPrinterWarnings(PrinterStatusInfo status,String s,int code) {
    if (code == Epos2CallbackCode.CODE_SUCCESS) {
      System.out.println("SUCCESS");
    }else{
      System.out.println("Error occured");
    }
  }

  private boolean ConnectedPrinter(String targetIP) {
    try {
      printer.connect(targetIP, Printer.PARAM_DEFAULT);
    } catch (Epos2Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  private boolean CheckStatus() {
    if(printer.getStatus().getOnline()==Printer.FALSE){
      return false;
    }
    if(printer.getStatus().getConnection()==Printer.FALSE){
      return false;
    }
    return true;
  }

  @Override
  public void onPtrStatusChange(Printer printer, int i) {
    this.printer=printer;
  }
  @ReactMethod
  public boolean printSampleReceipt() {
    if(!CheckStatus()){
      return false;
    }
    if (!createReceiptData()) {
      return false;
    }

    if (!printData()) {
      return false;
    }

    return true;
  }
  private boolean createReceiptData() {
    String method = "";
    StringBuilder textData = new StringBuilder();
    if (printer == null) {
      return false;
    }
    try {
      method = "addTextAlign";
      printer.addTextAlign(Printer.ALIGN_CENTER);
      method = "addFeedLine";
      printer.addFeedLine(1);
      textData.append("THE STORE 123 (555) 555 – 5555\n");
      textData.append("STORE DIRECTOR – John Smith\n");
      textData.append("\n");
      textData.append("7/01/07 16:58 6153 05 0191 134\n");
      textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
      textData.append("------------------------------\n");
      method = "addText";
      printer.addText(textData.toString());
      textData.delete(0, textData.length());

      textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
      textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
      textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
      textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
      textData.append("474 TRIPOD              8.99 R\n");
      textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
      textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
      textData.append("493 30L BLK FF DRESS   16.99 R\n");
      textData.append("407 LEVITATING DESKTOP  7.99 R\n");
      textData.append("441 **Blue Overprint P  2.99 R\n");
      textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
      textData.append("461 WESTGATE BLACK 25  59.99 R\n");
      textData.append("------------------------------\n");
      method = "addText";
      printer.addText(textData.toString());
      textData.delete(0, textData.length());

      textData.append("SUBTOTAL                160.38\n");
      textData.append("TAX                      14.43\n");
      method = "addText";
      printer.addText(textData.toString());
      textData.delete(0, textData.length());

      method = "addTextSize";
      printer.addTextSize(2, 2);
      method = "addText";
      printer.addText("TOTAL    174.81\n");
      method = "addTextSize";
      printer.addTextSize(1, 1);
      method = "addFeedLine";
      printer.addFeedLine(1);

      textData.append("CASH                    200.00\n");
      textData.append("CHANGE                   25.19\n");
      textData.append("------------------------------\n");
      method = "addText";
      printer.addText(textData.toString());
      textData.delete(0, textData.length());

      textData.append("Purchased item total number\n");
      textData.append("Sign Up and Save !\n");
      textData.append("With Preferred Saving Card\n");
      method = "addText";
      printer.addText(textData.toString());
      textData.delete(0, textData.length());
      method = "addFeedLine";
      printer.addFeedLine(2);



      method = "addCut";
      printer.addCut(Printer.CUT_FEED);
    }
    catch (Exception e) {
      printer.clearCommandBuffer();
      // ShowMsg.showException(e, method, mContext);
      return false;
    }

    textData = null;

    return true;
  }

  private boolean printData() {
    if (printer == null) {
      return false;
    }
    try {
      printer.sendData(Printer.PARAM_DEFAULT);
    }
    catch (Exception e) {
      printer.clearCommandBuffer();
      try {
        printer.disconnect();
      }
      catch (Exception ex) {
      }
      return false;
    }
    return true;
  }
}