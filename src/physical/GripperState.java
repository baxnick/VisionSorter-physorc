package physical;


public enum GripperState {
	INITIAL, 		// gripper constructed
	CALIBRATING,	// gripper in the process of calibration
	RELEASED, 		// gripper is ready for use
	RELEASING, 		// gripper is currently moving to a "released" position
	GRIPPED, 		// gripper is in a closed position
	GRIPPING		// gripper is moving toward a closed position
}
