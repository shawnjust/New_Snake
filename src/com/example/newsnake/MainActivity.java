package com.example.newsnake;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.opengl.GLES20;

public class MainActivity extends SimpleBaseGameActivity {

	private static int snakeSpeed = 200;

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	private Camera camera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	private TextureRegion mGrassBackground;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("image/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"head_new.png", 0, 0, 1, 1);
		this.mBitmapTextureAtlas.load();

		this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 1024, 1024);
		this.mGrassBackground = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxBackgroundTexture, this,
						"back1_new.png", 0, 0);
		this.mAutoParallaxBackgroundTexture.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base_new.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob_new.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f,
				new Sprite(0, 0, mGrassBackground, this
						.getVertexBufferObjectManager())));
		scene.setBackground(autoParallaxBackground);

		final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion
				.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion
				.getHeight()) / 2;
		final Sprite face = new Sprite(centerX, centerY,
				this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
		final MyPhysicsHandler physicsHandler = new MyPhysicsHandler(face);
		face.registerUpdateHandler(physicsHandler);

		scene.attachChild(face);

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				CAMERA_WIDTH
						- this.mOnScreenControlBaseTextureRegion.getWidth()
						- 50, CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight()
						- 20, this.camera,
				this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f, 200,
				this.getVertexBufferObjectManager(),
				new IAnalogOnScreenControlListener() {

					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {

						if (pValueX == 0 && pValueY == 0) {
							physicsHandler.setRadius(0);
						} else {
							physicsHandler.setSpeed(150);
							physicsHandler.setRadius(60);
						}

						// final MyRotation myRotation = new MyRotation();
						//
						// float base = (float) Math.sqrt(pValueX * pValueX
						// + pValueY * pValueY);
						// if (base == 0.0) {
						// physicsHandler.setAngularVelocity(0);
						// return;
						// }
						//
						// float x0 = physicsHandler.getVelocityX();
						// float y0 = physicsHandler.getVelocityY();
						// if (x0 == 0 && y0 == 0) {
						// x0 = snakeSpeed;
						// y0 = 0;
						// }
						// myRotation.setX0(x0);
						// myRotation.setY0(y0);
						// myRotation.setRotation((float) 0.5);
						//
						// physicsHandler.setVelocity(myRotation.getX1(),
						// myRotation.getY1());
						//
						// physicsHandler.setAngularVelocity(100);
						// face.setRotation(MathUtils.radToDeg((float)
						// Math.atan2(
						// -myRotation.getX1(), myRotation.getY1())));
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {
						face.registerEntityModifier(new SequenceEntityModifier(
								new ScaleModifier(0.25f, 1, 1.5f),
								new ScaleModifier(0.25f, 1.5f, 1)));
					}
				});
		analogOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(analogOnScreenControl);
		return scene;
	}

	class MyRotation {
		protected float x0 = 0, y0 = 0;
		protected float rotation = 0;

		public float getRotation() {
			return rotation;
		}

		public void setRotation(float rotation) {
			this.rotation = rotation;
		}

		public float getX0() {
			return x0;
		}

		public void setX0(float x0) {
			this.x0 = x0;
		}

		public float getY0() {
			return y0;
		}

		public void setY0(float y0) {
			this.y0 = y0;
		}

		public float getX1() {
			return (float) (x0 * Math.cos(rotation) - y0 * Math.sin(rotation));
		}

		public float getY1() {
			return (float) (x0 * Math.sin(rotation) + y0 * Math.cos(rotation));
		}

	}
}
