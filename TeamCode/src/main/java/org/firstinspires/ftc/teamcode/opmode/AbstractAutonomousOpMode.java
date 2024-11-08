/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opmode;

import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractAutonomousOpMode extends OpMode
{
  private final Team team;
  private final GameStrategy gameStrategy;
  private AutonomousState state = AutonomousState.HAVE_SPECIMEN;
  private int neutralSamplesLeft = 6;
  private int teamSamplesLeft = 3;
  ElapsedTime time = null;
  List<LynxModule> hubs;
  JoeBot robot = null;

  protected AbstractAutonomousOpMode( Team team, GameStrategy gameStrategy )
  {
    this.team = team;
    this.gameStrategy = gameStrategy;
  }

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    time = new ElapsedTime();

    //setup bulk reads
    hubs = hardwareMap.getAll( LynxModule.class );
    for( LynxModule module : hubs )
    {
      module.setBulkCachingMode( LynxModule.BulkCachingMode.MANUAL );
    }

    robot = new JoeBot( true, hardwareMap, telemetry );

    telemetry.addLine( "Initialized Auto" );
    telemetry.update();
  }

  @Override
  public void init_loop()
  {
    //Allow robot to be pushed around before the start button is pressed
    robot.drive().coast();
  }

  @Override
  public void start()
  {
    //Prevent robot from being pushed around
    robot.drive().brake();
  }

  @Override
  public void loop()
  {
    //Clear the BulkCache once per control cycle
    for( LynxModule module : hubs )
    {
      module.clearBulkCache();
    }

    switch( gameStrategy )
    {
      case PARK:
        park();
        break;
      case PLACE_SAMPLES_IN_BASKETS:
         bucketStrategy();
         break;
      case HANG_SPECIMENS_ON_BARS:
        specimenStrategy();
        break;
    }
  }

  private void park()
  {
    driveTo( Arrays.asList( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE.value, Math.toRadians( -90 ) ),
                            new Pose2d( Location.OBSERVATION_ZONE.value, Math.toRadians( -90 ) ) ) );
    robot.extensionArm().travelTo( ExtensionArm.Position.EXTEND_TO_TOUCH_BAR.value );

    //TODO - set state to parked
  }

  private void bucketStrategy()
  {
    if( state == AutonomousState.HAVE_SPECIMEN )
    {
      driveTo( new Pose2d( Location.SPECIMEN_BAR.value, 0 ) );
      robot.hangSpecimen( Bar.HIGH_BAR );
      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      //TODO - bad angle!
      driveTo( new Pose2d( Location.SAMPLE_BASKETS.value, -90 ) );
      //TODO - place sample in basket
      //TODO - state = have nothing
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      //TODO if neutral samples left <= 0 or there is not enough time yet park
//      private int neutralSamplesLeft = 6;
//      ElapsedTime time = null;

      //otherwise...
      //TODO - move to net further sample on ground away from fall
      //TODO - grab sample
      //TODO - state = HAVE_SAMPLE
      //TODO - decrement neutralSamplesLeft
    }
  }

  private void specimenStrategy()
  {
    if( state == AutonomousState.HAVE_SPECIMEN )
    {
      //TODO - Go to specimen bar
      //TODO - Hang specimen
      //TODO - state = HAVE_NOTHING
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      //TODO - Go to the observation zone
      //TODO - Give the sample to the human player
      //TODO - Back out of the loading zone
      //TODO - Wait for 5 seconds
      //TODO - Pick up the specimen.
      //TODO - state=HAVE_SPeciMENS
          /*
    TrajectoryActionBuilder test = drive.actionBuilder( drive.pose )
      .splineTo( new Vector2d( 10, 20 ), Math.toRadians( 90 ) )
      .waitSeconds(2)
      .lineToYSplineHeading(33, Math.toRadians(0))
      .setTangent(Math.toRadians(90))
      .lineToY(48)
      .lineToX(32)
      .strafeTo(new Vector2d(44.5, 30))
      .turn(Math.toRadians(180))
*/
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      //TODO if team samples left <= 0 or there is not enough time yet park
      //      private int teamSamplesLeft = 3;
      //      ElapsedTime time = null;

      //TODO - otherwise... what should we do?

    }
  }

  private void driveTo( Pose2d pose )
  {
    driveTo( Collections.singletonList( pose ) );
  }

  private void driveTo( List<Pose2d> poses )
  {
    MecanumDrive drive = robot.mecanumDrive();

    TrajectoryActionBuilder trajectory = drive.actionBuilder( drive.pose );
    for( Pose2d pose : poses )
    {
      trajectory = trajectory.splineTo( pose.position, pose.heading.toDouble() );
    }

    Actions.runBlocking( trajectory.build() );
  }
}
