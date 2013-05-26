package com.example.newsnake;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.entity.IEntity;

import android.util.Log;

public class MyPhysicsHandler extends BaseEntityUpdateHandler {

	protected float mSpeed;
	protected float mRadius;
	protected float mRotation;

	public void setRotation(float px, float py) {
		mRotation = (float) (Math.atan2(py, px) + Math.PI * 3 / 2);
		while (mRotation > Math.PI * 2) {
			mRotation -= Math.PI * 2;
		}
		while (mRotation < 0)
			mRotation += Math.PI;
	}

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
		if (this.mRadius != 0 && getCrossProduct(mRotation, theta0) != 0) {

			if (getCrossProduct(mRotation, theta0) > 0) {
				theta = theta0 - this.mSpeed / this.mRadius * pSecondsElapsed;
			} else {
				theta = theta0 + this.mSpeed / this.mRadius * pSecondsElapsed;
			}
			if (getCrossProduct(mRotation, theta0)
					* getCrossProduct(mRotation, theta) < 0) {
				theta = mRotation;
			}
		}
		while (theta > Math.PI * 2)
			theta -= Math.PI * 2;
		while (theta < 0)
			theta += Math.PI * 2;
		Log.v("atan2", "" + theta);
		pEntity.setRotation((float) (theta * 180 / Math.PI));
		float Vx = (float) (this.mSpeed * Math.sin(theta0));
		float Vy = (float) (this.mSpeed * Math.cos(theta0));
		float positionX = pEntity.getX() - Vx * pSecondsElapsed;
		float positionY = pEntity.getY() + Vy * pSecondsElapsed;
		pEntity.setPosition(positionX, positionY);
	}

	private float getCrossProduct(float theta1, float theta2) {
		return (float) (Math.cos(theta1) * Math.sin(theta2) - Math.sin(theta1)
				* Math.cos(theta2));
	}

	@Override
	public void reset() {
		mSpeed = 0.0f;
		mRadius = 0.0f;
		mRotation = 0.0f;
	}

}
