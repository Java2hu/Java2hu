package java2hu.object.bullet.phase;

/**
 * A phase animation is used for a bullet changing phases.
 * So either a spawn or death animation, or a transition between bullet types.
 */
public interface PhaseAnimation
{
	/**
	 * If the animation is currently playing, if so the main bullet is not drawn.
	 */
	public boolean isPlaying();
	
	/**
	 * Only checked if {@link PhaseAnimation#isPlaying()} is true.
	 * 
	 * If the bullet should check for intersection.
	 */
	public boolean hasHitbox();
	
	/**
	 * Start playing the animation, before this the animation should do nothing.
	 */
	public void start();
}
