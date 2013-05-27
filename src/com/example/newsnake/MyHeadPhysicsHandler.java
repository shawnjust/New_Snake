package com.example.newsnake;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.handler.BaseEntityUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import android.util.Log;

import com.example.newsnake.snakeinfo.PositionInformation;

public class MyHeadPhysicsHandler extends BaseEntityUpdateHandler {

	protected final float SPACE_TIME = 0.2f;
	protected final float HEAD_SPACE_TIME = 0.05f;

	protected float mSpeed;
	protected float mRadius;
	protected float mRotation;

	protected float contentTime;

	protected List<PositionInformation> bodyPositionList;
	protected List<Sprite> bodyList;

	public void addBody(Sprite pBody) {
		bodyList.add(pBody);
	}

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

	public MyHeadPhysicsHandler(IEntity pEntity) {
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

		contentTime += pSecondsElapsed;

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
		Log.v("atan2", "" + pSecondsElapsed);
		pEntity.setRotation((float) (theta * 180 / Math.PI));
		float Vx = (float) (this.mSpeed * Math.sin(theta0));
		float Vy = (float) (this.mSpeed * Math.cos(theta0));
		float positionX = pEntity.getX() - Vx * pSecondsElapsed;
		float positionY = pEntity.getY() + Vy * pSecondsElapsed;
		pEntity.setPosition(positionX, positionY);

		PositionInformation pos = new PositionInformation();
		pos.setPositionX(positionX + ((Sprite) pEntity).getWidth() / 2);
		pos.setPositionY(positionY + ((Sprite) pEntity).getHeight() / 2);
		pos.setRotation((float) (theta * 180 / Math.PI));
		pos.setTime(contentTime);

		bodyPositionList.add(0, pos);

		Iterator<PositionInformation> itPos = bodyPositionList.iterator();
		Iterator<Sprite> itBody = bodyList.iterator();
		int count = 0;

		PositionInformation currentpos = pos, previouspos = pos, workpos = pos;
		while (itBody.hasNext()) {
			Sprite body = itBody.next();
			++count;
			previouspos = currentpos;
			while (itPos.hasNext()) {
				currentpos = itPos.next();
				if (contentTime - currentpos.getTime() > SPACE_TIME * count + HEAD_SPACE_TIME) {
					float abs1, abs2;
					abs1 = Math.abs(contentTime - currentpos.getTime()
							- SPACE_TIME * count+ HEAD_SPACE_TIME);
					abs2 = Math.abs(contentTime - previouspos.getTime()
							- SPACE_TIME * count+ HEAD_SPACE_TIME);

					workpos = new PositionInformation();
					workpos.setPositionX((currentpos.getPositionX() * abs2 + previouspos
							.getPositionX() * abs1)
							/ (abs1 + abs2));
					workpos.setPositionY((currentpos.getPositionY() * abs2 + previouspos
							.getPositionY() * abs1)
							/ (abs1 + abs2));
					workpos.setRotation(currentpos.getRotation());
					break;
				}
				previouspos = currentpos;
				workpos = currentpos;
			}

			body.setPosition(workpos.getPositionX() - body.getWidth() / 2,
					workpos.getPositionY() - body.getHeight() / 2);
			body.setRotation(workpos.getRotation());
		}

		while (itPos.hasNext()) {
			itPos.next();
			itPos.remove();
		}
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
		contentTime = 0.0f;
		bodyPositionList = new LinkedList<PositionInformation>();
		bodyList = new LinkedList<Sprite>();
	}

}
