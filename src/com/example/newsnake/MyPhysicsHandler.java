package com.example.newsnake;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.entity.IEntity;

public class MyPhysicsHandler extends BaseEntityUpdateHandler {

	protected float mSpeed;
	protected float mRadius;
	protected float mRotation;

	public float getmRotation() {
		return mRotation;
	}

	public void setRotation(float mRotation) {
		this.mRotation = mRotation;
	}

	public MyPhysicsHandler(IEntity pEntity) {
		super(pEntity);
		this.reset();
	}

	public float getSpeed() {
		return mSpeed;
	}

	public void setSpeed(float mSpeed) {
		this.mSpeed = mSpeed;
	}

	public float getRadius() {
		return mRadius;
	}

	public void setRadius(float mRadius) {
		this.mRadius = mRadius;
	}

	@Override
	protected void onUpdate(final float pSecondsElapsed, final IEntity pEntity) {
		float theta0 = (float) (pEntity.getRotation() * Math.PI / 180);
		float theta = theta0;
		if (this.mRadius != 0) {
			theta = theta0 + this.mSpeed / this.mRadius * pSecondsElapsed;
		}
		pEntity.setRotation((float) (theta * 180 / Math.PI));
		float Vx = (float) (this.mSpeed * Math.sin(theta0));
		float Vy = (float) (this.mSpeed * Math.cos(theta0));
		float positionX = pEntity.getX() - Vx * pSecondsElapsed;
		float positionY = pEntity.getY() + Vy * pSecondsElapsed;
		pEntity.setPosition(positionX, positionY);
		
		pEntity.get
	}

	@Override
	public void reset() {
		mSpeed = 0.0f;
		mRadius = 0.0f;
		mRotation = 0.0f;
	}

}
