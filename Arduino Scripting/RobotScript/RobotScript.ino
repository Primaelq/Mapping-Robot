#include <SoftwareSerial.h>
#include <AFMotor.h>
#include <Servo.h>

//SoftwareSerial BTSerial (19, 18); // Rx, Tx

const int trigPin = 39;
const int echoPin = 45;

AF_DCMotor motorRight (3);
AF_DCMotor motorLeft (4);

Servo servo1;

long obstacleDistance;

long rightObstacleDistance = 0;
long leftObstacleDistance = 0;

long lecture_echo;

int i = 0;
int instruction = 0;

int rightIR = 0;
int leftIR = 0;

bool turningRight = false;

bool rightWheelRay = false;

enum State
{
  forward,
  backward,
  right,
  left,
  stand
};

State state;

void setup()
{
  Serial.begin (9600);
  Serial1.begin (9600);

  servo1.attach(9);
  servo1.write (90);

  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  pinMode(echoPin, INPUT);

  motorRight.setSpeed (75);
  motorRight.run (RELEASE);

  motorLeft.setSpeed (75);
  motorLeft.run (RELEASE);

  state = stand;

  delay (8000);
}

void loop()
{
  DistanceCalculation ();

  Serial.println (obstacleDistance);

  /*motorRight.run (FORWARD);
  motorLeft.run (FORWARD);

  if (i <= 50)
  {
    turningRight = true;
  }
  else if (i >= 130)
  {
    turningRight = false;
  }

  if (turningRight)
  {
    LookRight ();
  }
  else
  {
    LookLeft ();
  }

  servo1.write (i);
    
  if (obstacleDistance < 20 && i > 40)
  {
    TurnLeft ();
  }
  else if (obstacleDistance < 20 && i < 40)
  {
    TurnRight ();
  }
  
  delay (5);

  Serial.println (map(analogRead(A8), 0, 1000, 0, 1));
  Serial.println (analogRead(A8));

  rightIR = analogRead(A8);
  leftIR = analogRead(A9);

  delay (10);

  if (rightIR < 100 && rightWheelRay == false)
  {
    rightWheelRay = true;
    
  }
  
  if (Serial1.available () > 0)
  {
    //Serial.write (Serial1.read ());
    instruction = Serial1.parseInt ();
  }
  
  if(Serial.available () > 0)
  {
    Serial1.write (Serial.read ());
  }

  switch (instruction)
  {
    case 1:
      state = forward;
      break;
      
    case 2:
      state = backward;
      break;
      
    case 3:
      state = right;
      break;
      
    case 4:
      state = left;
      break;
      
    case 5:
      state = stand;
      break;

    default:
      state = stand;
      break;
  }

  //Serial.println (instruction);
  //Serial1.write (state);

  switch (state)
  {
    case forward:
      GoForward ();
      break;

    case backward:
      GoBackward ();
      break;

    case right:
      TurnRight ();
      break;

    case left:
      TurnLeft ();
      break;

    case stand:
      GoForward ();
  }*/

  /*if (obstacleDistance > 20)
  {
    GoForward ();
  }
  else
  {
    Stand ();
  }*/
  
  /*
  else
  {
    Stand ();

    servo1.write (0);

    delay (2000);

    servo1.write (180);
    
  }*/
  
}

// Turn Servo Right
void LookRight ()
{
  i++;
}

// Turn Servo Left
void LookLeft ()
{
  i--;
}

void GoForward ()
{
  motorRight.run (FORWARD);
  motorLeft.run (FORWARD);
}

void GoBackward ()
{
  motorRight.run (BACKWARD);
  motorLeft.run (BACKWARD);
}

void TurnRight ()
{
  motorRight.run (BACKWARD);
  motorLeft.run (FORWARD);
}

void TurnLeft ()
{
  motorRight.run (FORWARD);
  motorLeft.run (BACKWARD);
}

void Stand ()
{
  motorRight.run (RELEASE);
  motorLeft.run (RELEASE);
}

void DistanceCalculation ()
{
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  lecture_echo = pulseIn(echoPin, HIGH);
  obstacleDistance = lecture_echo / 58;
  //Serial1.write (obstacleDistance);
}
