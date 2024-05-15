#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// Replace with your network credentials
const char* ssid = "ESP8266";
const char* password = "R12345678R";

ESP8266WebServer server(80);

int ledPins[] = {D0, D1, D2, D3}; // Change these to the actual pins used for LEDs

void setup() {
  Serial.begin(115200);

  // Set LED pins as outputs
  for (int i = 0; i < 4; i++) {
    pinMode(ledPins[i], OUTPUT);
    digitalWrite(ledPins[i], LOW);
  }

  // Connect to WiFi
  WiFi.softAP(ssid, password);
  Serial.println("Access Point Started");

  // Define server routes
  server.on("/", handleRoot);
  for (int i = 0; i <= 10; i++) {
    server.on(String("/led/") + i, [i]() { handleLed(i); });
  }
  server.on("/reset", handleReset);

  // Start server
  server.begin();
  Serial.println("HTTP server started");

  Serial.print("AP IP address: ");
  Serial.println(WiFi.softAPIP()); // Display AP IP address
}

void loop() {
  server.handleClient();
}

void handleRoot() {
  server.send(200, "text/plain", "ESP8266 LED Control");
}

void handleLed(int num) {
  for (int i = 0; i < 4; i++) {
    digitalWrite(ledPins[i], (num >> i) & 0x01);
  }
  server.send(200, "text/plain", "LEDs set to " + String(num));
}

void handleReset() {
  for (int i = 0; i < 4; i++) {
    digitalWrite(ledPins[i], LOW);
  }
  server.send(200, "text/plain", "LEDs reset");
}
