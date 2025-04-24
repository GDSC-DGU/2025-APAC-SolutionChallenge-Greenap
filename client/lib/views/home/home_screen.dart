import 'package:flutter/material.dart';
import 'package:greenap/config/fontSystem.dart';
import 'package:greenap/config/colorSystem.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('지속 가능한 하루, 이정선 님과 함께!')),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(children: [
          ],
        ),
      ),
    );
  }
}
