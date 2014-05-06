/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.lego.mindstorm.nxt.sensors;

import android.util.Log;

import org.catrobat.catroid.lego.mindstorm.MindstormConnection;
import org.catrobat.catroid.lego.mindstorm.MindstormException;

public class NXTI2CUltraSonicSensor extends NXTI2CSensor {
	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private DistanceUnit distanceUnit;

	private enum DistanceUnit {
		DUMMY, CENTIMETER, INCH;
	}

	private enum SensorRegister
	{
		Version(0x00), ProductId(0x08), SensorType(0x10), FactoryZeroValue(0x11), FactoryScaleFactor(0x12),
				FactoryScaleDivisor(0x13), MeasurementUnits(0x14),
				Interval(0x40), Command(0x41), Result1(0x42), Result2(0x43), Result3(0x44), Result4(0x45), Result5(0x46),
				Result6(0x47), Result7(0x48), Result8(0x49), ZeroValue(0x50), ScaleFactor(0x51), ScaleDivisor(0x52);

		private int register;

		private SensorRegister(int register) {
			this.register = register;
		}

			public byte getByte() {
			return (byte) register;
		}
	}

	private enum UltrasonicCommand {
		Off(0x00), SingleShot(0x01), Continuous(0x02), EventCapture(0x03), RequestWarmReset(0x04);

		private int command;

		private UltrasonicCommand(int command) {
			this.command = command;
		}

		public byte getByte() {
			return (byte) command;
		}
	}

	public NXTI2CUltraSonicSensor(MindstormConnection connection) {
		super(ULTRASONIC_ADDRESS, NXTSensorType.LOW_SPEED_9V, connection);
		distanceUnit = DistanceUnit.CENTIMETER;
	}

	public NXTI2CUltraSonicSensor(DistanceUnit distanceUnit, MindstormConnection connection) {
		super(ULTRASONIC_ADDRESS, NXTSensorType.LOW_SPEED_9V, connection);
		this.distanceUnit = distanceUnit;
	}

	@Override
	protected void initialize()
	{
		super.initialize();
	}

	public void singleShot(boolean reply) {
		setMode(UltrasonicCommand.SingleShot, reply);
	}

	public void TurnOffSonar() {
		setMode(UltrasonicCommand.Off, false);
	}

	public void Continuous() {
		setMode(UltrasonicCommand.Continuous, false);
	}

	public boolean IsSensorOff() {
		if (getMode() == UltrasonicCommand.Off)
			return true;
		return false;
	}

	public void reset() {
		setMode(UltrasonicCommand.RequestWarmReset, false);
	}

	private byte getContinuousInterval() {
		return readRegister(SensorRegister.Interval.getByte(), 1)[0];
	}

	private void setContinuousInterval(byte interval)
	{
		writeRegister(SensorRegister.Interval.getByte(), interval, false);
		super.wait(60);
	}

//	private SonarSettings GetFactorySettings() {
//		return new SonarSettings(   ReadRegister((byte)SonarRegister.FactoryZeroValue, 1)[0],
//				ReadRegister((byte)SonarRegister.FactoryScaleFactor, 1)[0],
//				ReadRegister((byte)SonarRegister.FactoryScaleDivisor, 1)[0]);
//	}
//
//	private SonarSettings GetActualSettings() {
//		return new SonarSettings(   ReadRegister((byte)SonarRegister.ZeroValue, 1)[0],
//				ReadRegister((byte)SonarRegister.ScaleFactor, 1)[0],
//				ReadRegister((byte)SonarRegister.ScaleDivisor, 1)[0]);
//	}

	private UltrasonicCommand getMode() {
		return UltrasonicCommand.Continuous.values()[readRegister(SensorRegister.Command.getByte(), 1)[0]];
	}

	private void setMode(UltrasonicCommand command, boolean reply) {
		writeRegister(SensorRegister.Command.getByte(), command.getByte(), reply);
		super.wait(60);
	}

	private static final String TAG = NXTI2CUltraSonicSensor.class.getSimpleName();
	private static final int DEFAULT_VALUE = 255;
	private int lastValidReading = DEFAULT_VALUE;

	@Override
	public int getValue() {
		try {
			lastValidReading = readRegister(SensorRegister.Result1.getByte(), 1)[0] & 0xFF;
			return getValue(lastValidReading);

		} catch (MindstormException e) {
			Log.e(TAG, e.getMessage());
			return getValue(lastValidReading);
		}
	}

	private int getValue(int value) {
		if (distanceUnit == DistanceUnit.INCH) {
			return (value * 39370) / 1000;
		}
		return value;
	}
}