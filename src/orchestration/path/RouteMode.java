package orchestration.path;

public enum RouteMode
{
	TEMPORARY_FAILURE, PERMANENT_FAILURE, INDIRECT, DIRECT;

	public boolean success()
	{
		return this == RouteMode.DIRECT || this == RouteMode.INDIRECT;
	}
}
