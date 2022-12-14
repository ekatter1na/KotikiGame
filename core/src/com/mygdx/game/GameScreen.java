package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
	Drop game;

	Texture donetImage;
	TextureRegion backgroundTexture;
	Texture ggImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	int dropsGathered;

	public GameScreen(final Drop gam) {
		this.game = gam;

		
		donetImage = new Texture(Gdx.files.internal("donet.png"));
		ggImage = new Texture(Gdx.files.internal("gg.png"));
		backgroundTexture = new TextureRegion(new Texture("background.jpg"), 0, 0, 1200, 799);

		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);

		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 240);

		
		bucket = new Rectangle();
		
		bucket.x = 400 / 2 - 64 / 2;
		
		bucket.y = 20;

		bucket.width = 32;
		bucket.height = 32;

		
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 400 - 64);
		raindrop.y = 240;
		raindrop.width = 32;
		raindrop.height = 32;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		camera.update();

		
		game.batch.setProjectionMatrix(camera.combined);

		
		game.batch.begin();
		game.batch.draw(backgroundTexture, 0, 0);
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
		game.batch.draw(ggImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			game.batch.draw(donetImage, raindrop.x, raindrop.y);
		}
		game.batch.end();

		
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket.x += 200 * Gdx.graphics.getDeltaTime();

		
		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > 400 - 64)
			bucket.x = 400 - 64;

		
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();

		
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
				iter.remove();
			if (raindrop.overlaps(bucket)) {
				dropsGathered++;
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		donetImage.dispose();
		ggImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}
