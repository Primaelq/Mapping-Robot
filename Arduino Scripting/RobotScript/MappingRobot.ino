#include <AFMotor.h>
#include <Wire.h>
#include <I2Cdev.h>
#include <HMC5883L.h>

AF_DCMotor motorRight (3);
AF_DCMotor motorLeft (4);

//----------Ultrasonic sensors pins (Digital)----------//
// Front
const int trigPinFront = 45;
const int echoPinFront = 47;
// Right
const int trigPinRight = 53;
const int echoPinRight = 51;
// Left
const int trigPinLeft = 35;
const int echoPinLeft = 33;

//----------Laser sensors pins (Analog)----------//

const int laserSensorRight = 10;
const int laserSensorLeft = 9;

//----------Variables to keep sonar and distances values----------//
long obstacleDistance;
long lecture_echo;

//----------Variables for laser sensors----------//

bool ray[2] = {false, false};
int rayCount[2] = {0, 0};

#define RIGHT 0
#define LEFT 1

//----------Wait for bluetooth signal----------//
bool go = false;

//----------Motor speeds----------//
int leftSpeed = 125;
int rightSpeed = 125; 

//----------Declare magnetometer----------//
HMC5883L mag;
int16_t mx, my, mz;

void setup()
{
  //----------Initiating Serials (serial1 -> Bluetooth)----------//
  Serial.begin (9600);
  Serial1.begin (9600);

  Wire.begin();

  //----------Setting motor speeds and state----------//
  motorRight.setSpeed (rightSpeed);
  motorRight.run (RELEASE);

  motorLeft.setSpeed (leftSpeed);
  motorLeft.run (RELEASE);

  //----------Initiating ultrasonic sensor's pins----------//
  pinMode(trigPinFront, OUTPUT);
  digitalWrite(trigPinFront, LOW);
  pinMode(echoPinFront, INPUT);

  pinMode(trigPinRight, OUTPUT);
  digitalWrite(trigPinRight, LOW);
  pinMode(echoPinRight, INPUT);

  pinMode(trigPinLeft, OUTPUT);
  digitalWrite(trigPinLeft, LOW);
  pinMode(echoPinLeft, INPUT);

  //----------Initiating laser sensor's pins----------//
  pinMode(laserSensorLeft, INPUT);
  pinMode(laserSensorRight, INPUT);

  //----------Testing magnetometer----------//
  Serial.println("Initializing I2C devices...");
  mag.initialize();

  // verify connection
  Serial.println("Testing device connections...");
  Serial.println(mag.testConnection() ? "HMC5883L connection successful" : "HMC5883L connection failed");

  //----------Wait 5 seconds (let time to connect batteries)----------//
  delay (5000);
}

void loop()
{
  //----------Call method to get obstacle distances----------//
  //getSonarDistance(trigPinFront, echoPinFront);

  //----------Call method to get robot rotation----------//
  //getRotation();

  //----------Call method to get wheel speed----------//
  getWheelSpeed(laserSensorRight, RIGHT);
  Serial.print("\t");
  getWheelSpeed(laserSensorLeft, LEFT);

  motorRight.run (FORWARD);
  motorLeft.run (FORWARD);
}

void getSonarDistance(int trig, int echo)
{
  static long startMillis;
  //----------Transmit ultrasounds for 10 microseconds and stop----------//
  unsigned long currentMillis = millis();
  digitalWrite(trig, HIGH);
  if (millis() - startMillis >= 10)
  {
    digitalWrite(trig, LOW);
  }
  else
  {
    startMillis += currentMillis;
  }
  
  //----------Retrieve time between transmition and reception to get distance (d=v*t)----------//
  lecture_echo = pulseIn(echo, HIGH);
  obstacleDistance = lecture_echo / 58;
  Serial.println(obstacleDistance);
}

void getRotation()
{
  //----------read raw heading measurements from device----------//
  mag.getHeading(&mx, &my, &mz);

  /* display tab-separated gyro x/y/z values
  Serial.print("mag:\t");
  Serial.print(mx); Serial.print("\t");
  Serial.print(my); Serial.print("\t");
  Serial.print(mz); Serial.print("\t");*/
    
  //----------To calculate heading in degrees. 0 degree indicates North----------//
  float heading = atan2(my, mx);
  heading += 0.0;
  if(heading < 0)
    heading += 2 * M_PI;
  Serial.print("heading:\t");
  Serial.println(heading * 180/M_PI);
}

void getWheelSpeed (int laserOut, int side)
{
  //----------Getting values from analog pins of the laser sensors----------//
  int val = analogRead(laserOut);

  if (val < 500)
  {
    if (ray[side] == false)
    {
      rayCount[side]++;
      adjustWheelSpeed();
    }
    ray[side] = true;
  }
  else
  {
    ray[side] = false;
  }
  
  Serial.println(rayCount[side]);
  
}

void sendOverBluetooth (char encoder,char msg)
{
  Serial1.write ((byte) encoder);
  Serial1.write (msg);
  Serial1.write ((byte) '#');
}

void sendIntOverBluetooth (char encoder, int i)
{
  Serial1.write ((byte) encoder);
  Serial1.write (i);
  Serial1.write ((byte) '#');
}

void adjustWheelSpeed ()
{
  if (rayCount[LEFT] > rayCount[RIGHT])
  {
    rightSpeed ++;
    leftSpeed --;
    Serial.println("Adjusted Right ++");
  }
  else if (rayCount[RIGHT] > rayCount[LEFT])
  {
    leftSpeed ++;
    rightSpeed --;
    Serial.println("Adjusted Left ++");
  }
  else
  {
    rightSpeed = 125;
    leftSpeed = 125;
  }
  
  motorRight.setSpeed (rightSpeed);
  motorLeft.setSpeed (leftSpeed);
}


