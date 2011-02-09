package orchestration.errand;

public enum ErrandState
{
	FETCHING,  /// The bot is heading to the ball and gripping it
	RETURNING, /// The bot is returning the gripped ball to a goal
	VISION,    /// The bot is moving to a location where vision is present 
	DELAYED,   /// The bot is avoiding a collision with another bot
	ABANDONED, /// The errand cannot be carried out and must be discarded
	COMPLETED  /// The errand has been completed successfully
}
