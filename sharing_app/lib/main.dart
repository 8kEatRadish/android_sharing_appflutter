import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:sharing_app/util/toast.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Share App'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel("samples.flutter.io/battery");
  var isLoading = 0;
  List configs;

  void _clickEvent(){
    setState(() {
      print("start getConfig");
      isLoading = 1;
    });
    _getConfig();
  }

  Future<Null> _getConfig() async {
    List config;
    try {
      //在通道上调用此方法
      final List result = await platform.invokeMethod("getConfig");
      for(var i = 0; i < result.length; i++){
        print("_getConfig" + result[i]);
      }
      config = result;
    } on PlatformException catch (e) {
      print("Failed to get application config : '${e.message}'.");
    }
    setState(() {
      print("dart -setState");
      configs = config;
      isLoading = 2;
    });
  }

  Widget _listViewBuilder(BuildContext buildContext, int index){
    var packageName = configs[index].split(" ")[0];
    var applicationName = configs[index].split(" ")[1];
    return GestureDetector(
      onTap: (){
        print(applicationName);
        Toast.show(buildContext,"share > " + applicationName);
        try{
          platform.invokeMethod("share",{"item" : index});
        }on PlatformException catch (e){
          print("Failed to shart apk : '${e.message}'.");
        }
      },
      child: Column(
        children: <Widget>[
          Text(
            applicationName,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 20.0,
              fontWeight: FontWeight.bold,
              color: Colors.blue,
            ),
          ),
          Text(
            packageName,
            textAlign: TextAlign.center,
          ),
          Divider()
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if(isLoading == 2){
      return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: Center(
          child: new ListView.builder(
            padding: new EdgeInsets.symmetric(
              horizontal: 20,
            ),
            itemCount: configs.length,
            itemBuilder: _listViewBuilder,
          ),
        ),
      );
    }else if(isLoading == 0){
      return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              RaisedButton(
                onPressed: _clickEvent,
                child: new Text("获取已安装应用"),
              ),
            ],
          ),
        ),
      );
    }else if(isLoading == 1){
      return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: Center(
          child: new Text("loading..."),
        ),
      );
    }
  }
}
