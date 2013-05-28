package com.example.newsnake;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
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

	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	private Camera camera;

	private BitmapTextureAtlas mHeadBitmapTextureAtlas;
	private TiledTextureRegion mHeadTextureRegion;

	private BitmapTextureAtlas mBodyBitmapTextureAtlas;
	private TiledTextureRegion mBodyTextureRegion;

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	private TextureRegion mGrassBackground;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private BitmapTextureAtlas mFoodBitmapTextureAtlas;
	private TiledTextureRegion mFoodTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("image/");

		this.mHeadBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.mHeadTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mHeadBitmapTextureAtlas, this,
						"head_new.png", 0, 0, 1, 1);
		this.mHeadBitmapTextureAtlas.load();

		this.mBodyBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.mBodyTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBodyBitmapTextureAtlas, this,
						"body_new.png", 0, 0, 1, 1);
		this.mBodyBitmapTextureAtlas.load();

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

		this.mFoodBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128);
		this.mFoodTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mFoodBitmapTextureAtlas, this,
						"food1_new.png", 0, 0, 1, 1);
		this.mFoodBitmapTextureAtlas.load();
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

		final float headCenterX = CAMERA_WIDTH / 4
				- this.mHeadTextureRegion.getWidth() / 2;
		final float headCenterY = CAMERA_HEIGHT / 4
				- this.mHeadTextureRegion.getHeight() / 2;
		final Sprite head = new Sprite(headCenterX, headCenterY,
				this.mHeadTextureRegion, this.getVertexBufferObjectManager());
		final MyHeadPhysicsHandler physicsHandler = new MyHeadPhysicsHandler(
				head);
		head.registerUpdateHandler(physicsHandler);
		scene.attachChild(head);
		head.setZIndex(10000);

		float foodCenterX = (float) (Math.random() * CAMERA_WIDTH - this.mFoodTextureRegion
				.getWidth() / 2);
		float foodCenterY = (float) (Math.random() * CAMERA_HEIGHT - this.mFoodTextureRegion
				.getHeight() / 2);
		final Sprite food = new Sprite(foodCenterX, foodCenterY,
				mFoodTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(food);

		final float bodyCenterX = (CAMERA_WIDTH / 4 - this.mBodyTextureRegion
				.getWidth() / 2);
		final float bodyCenterY = (CAMERA_HEIGHT / 4 - this.mBodyTextureRegion
				.getHeight() / 2);
		final Sprite body1 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body2 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body3 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body4 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body5 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body6 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		final Sprite body7 = new Sprite(bodyCenterX, bodyCenterY,
				this.mBodyTextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(body1);
		scene.attachChild(body2);
		scene.attachChild(body3);
		scene.attachChild(body4);
		scene.attachChild(body5);
		scene.attachChild(body6);
		scene.attachChild(body7);

		body1.setZIndex(9999);
		body2.setZIndex(9998);
		body3.setZIndex(9997);
		body4.setZIndex(9996);
		body4.setZIndex(9995);
		body4.setZIndex(9994);
		body4.setZIndex(9993);

		physicsHandler.addBody(body1);
		physicsHandler.addBody(body2);
		physicsHandler.addBody(body3);
		physicsHandler.addBody(body4);
		physicsHandler.addBody(body5);
		physicsHandler.addBody(body6);
		physicsHandler.addBody(body7);

		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(float arg0) {
				float foodPosX = food.getX() + food.getWidth() / 2;
				float foodPosY = food.getY() + food.getHeight() / 2;
				float headPosX = head.getX() + head.getWidth() / 2;
				float headPosY = head.getY() + head.getHeight() / 2;
				if ((foodPosX - headPosX) * (foodPosX - headPosX)
						+ (foodPosY - headPosY) * (foodPosY - headPosY) < 20 * 20) {
					Sprite body = new Sprite(bodyCenterX, bodyCenterY,
							MainActivity.this.mBodyTextureRegion,
							MainActivity.this.getVertexBufferObjectManager());
					scene.attachChild(body);
					body.setZIndex(9999);
					physicsHandler.addBody(body);
					float foodCenterX = (float) (Math.random() * CAMERA_WIDTH - MainActivity.this.mFoodTextureRegion
							.getWidth() / 2);
					float foodCenterY = (float) (Math.random() * CAMERA_HEIGHT - MainActivity.this.mFoodTextureRegion
							.getHeight() / 2);
					food.setPosition(foodCenterX, foodCenterY);
				}

			}
		});

		scene.sortChildren();

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				CAMERA_WIDTH
						- this.mOnScreenControlBaseTextureRegion.getWidth()
						- 70, CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight()
						- 40, this.camera,
				this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f, 200,
				this.getVertexBufferObjectManager(),
				new IAnalogOnScreenControlListener() {

					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						physicsHandler.setRotation(pValueX, pValueY);
						if (pValueX == 0 && pValueY == 0) {
							physicsHandler.setRadius(0);
						} else {
							physicsHandler.setSpeed(150);
							physicsHandler.setRadius(60);
						}
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {
						head.registerEntityModifier(new SequenceEntityModifier(
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
}
