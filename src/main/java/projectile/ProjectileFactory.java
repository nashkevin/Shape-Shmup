package main.java.projectile;

public class ProjectileFactory {
	
	private transient Environment environment;
	private Agent owner;
	private Point position;
	private Vector2D velocity;

	private int damage;
	private int speed;
	private double size;

	public ProjectileFactory(Environment environment, Agent owner, int damage,
		int speed, int size
	) {
		this.environment = environment;
		this.owner = owner;
		this.damage = damage;
		this.speed = speed;
		this.size = size;
	}

	public void increaseDamage(int amount) {
		this.damage += amount;
	}

	public void increaseSpeed(int amount) {
		this.speed += amount;
	}

	public void increaseSize(double amount) {
		this.size += amount;
	}
 
	public Projectile spawn() {
		return new projectile(environment, owner, damage, speed, size);
	}
}
