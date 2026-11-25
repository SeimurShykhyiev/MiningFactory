package com.miningfactory.core;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.miningfactory.conveyor.Conveyor;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MachineShop;
import com.miningfactory.machines.Furnace;
import com.miningfactory.machines.Machine;
import com.miningfactory.machines.OreBox;
import com.miningfactory.player.Player;
import com.miningfactory.resources.ResourceStack;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Building;
import com.miningfactory.world.Map;
import com.miningfactory.world.Tile;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class GameScreen implements Screen {
    public static final int SCREEN_W = 1920;
    public static final int SCREEN_H = 1080;
    public static final int MAP_PIXEL_W = GameWorld.GRID_COLS * Map.TILE_SIZE; // 21 * 64 = 1344
    public static final int MAP_PIXEL_H = GameWorld.GRID_ROWS * Map.TILE_SIZE; // 16 * 64 = 1024

    private final GameWorld world;

    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final java.util.Map<String, Texture> textures = new HashMap<>();

    private Building hoveredBuilding = null;
    private int hoverGx = -1;
    private int hoverGy = -1;



    public GameScreen() {
        this.world = new GameWorld();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, SCREEN_W, SCREEN_H);
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // Генерація шрифту з підтримкою кирилиці
        this.font = createCyrillicFont();

        loadAllTextures();
    }

    private BitmapFont createCyrillicFont() {
        // ЖЕЛЕЗОБЕТОННЫЙ СПОСОБ: берём шрифт прямо из системной папки Windows
        FileHandle fontFile = Gdx.files.absolute("C:/Windows/Fonts/arial.ttf");

        if (!fontFile.exists()) {
            System.out.println("Шрифт не найден по абсолютному пути!");
            BitmapFont fallback = new BitmapFont();
            fallback.getData().setScale(1.35f);
            return fallback;
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        // Подключаем кириллицу
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяІіЇїЄєҐґ";

        BitmapFont cyrillicFont = generator.generateFont(parameter);
        generator.dispose();

        return cyrillicFont;
    }

    private void loadAllTextures() {
        tryLoadTexture("floor_grass.png", true);
        tryLoadTexture("floor_factory.png", true);
        tryLoadTexture("floor_mine.png", true);

        for (ResourceType r : ResourceType.values()) {
            tryLoadTexture(r.getTexturePath(), false);
        }
        for (MachineShop.CatalogItem item : MachineShop.CatalogItem.values()) {
            tryLoadTexture(item.getTextureName(), false);
        }

        tryLoadTexture("miner_up.png", false);
        tryLoadTexture("miner_down.png", false);
        tryLoadTexture("miner_left.png", false);
        tryLoadTexture("miner_right.png", false);
        tryLoadTexture("miner.png", false);
    }

    private void tryLoadTexture(String fileName, boolean isFloorTile) {
        if (fileName == null) return;
        String[] prefixes = {"", "textures/", "miner_sprites/", "machines/", "resources/", "floors/"};
        for (String prefix : prefixes) {
            FileHandle fh = Gdx.files.internal(prefix + fileName);
            if (fh.exists()) {
                try {
                    Pixmap raw = new Pixmap(fh);
                    Pixmap processed = isFloorTile ? cropFloorMargins(raw) : cleanBackgroundAndTrim(raw);
                    raw.dispose();

                    Texture tex = new Texture(processed);
                    tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    processed.dispose();

                    textures.put(fileName, tex);
                    return;
                } catch (Exception ignored) {
                }
            }
        }
    }

    private Pixmap cropFloorMargins(Pixmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int marginX = (int) (w * 0.12f);
        int marginY = (int) (h * 0.12f);
        int cw = w - marginX * 2;
        int ch = h - marginY * 2;

        Pixmap cropped = new Pixmap(cw, ch, Pixmap.Format.RGBA8888);
        cropped.drawPixmap(src, 0, 0, marginX, marginY, cw, ch);
        return cropped;
    }

    private Pixmap cleanBackgroundAndTrim(Pixmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Pixmap rgba = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        rgba.setBlending(Pixmap.Blending.None);
        rgba.drawPixmap(src, 0, 0);

        boolean[][] visited = new boolean[w][h];
        Queue<int[]> queue = new ArrayDeque<>();

        for (int x = 0; x < w; x++) {
            enqueueIfDarkOrTransparentBorder(rgba, x, 0, visited, queue);
            enqueueIfDarkOrTransparentBorder(rgba, x, h - 1, visited, queue);
        }
        for (int y = 0; y < h; y++) {
            enqueueIfDarkOrTransparentBorder(rgba, 0, y, visited, queue);
            enqueueIfDarkOrTransparentBorder(rgba, w - 1, y, visited, queue);
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        while (!queue.isEmpty()) {
            int[] pt = queue.poll();
            int cx = pt[0];
            int cy = pt[1];
            rgba.drawPixel(cx, cy, 0x00000000);

            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny]) {
                    enqueueIfDarkOrTransparentBorder(rgba, nx, ny, visited, queue);
                }
            }
        }

        int minX = w, minY = h, maxX = -1, maxY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = rgba.getPixel(x, y);
                int alpha = pixel & 0xFF;
                if (alpha > 25) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return rgba;
        }

        int bw = maxX - minX + 1;
        int bh = maxY - minY + 1;
        int size = Math.max(bw, bh);
        Pixmap trimmed = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        trimmed.setBlending(Pixmap.Blending.None);
        int offsetX = (size - bw) / 2;
        int offsetY = (size - bh) / 2;
        trimmed.drawPixmap(rgba, offsetX, offsetY, minX, minY, bw, bh);
        rgba.dispose();
        return trimmed;
    }

    private void enqueueIfDarkOrTransparentBorder(Pixmap p, int x, int y, boolean[][] visited, Queue<int[]> q) {
        int pixel = p.getPixel(x, y);
        int r = (pixel >>> 24) & 0xFF;
        int g = (pixel >>> 16) & 0xFF;
        int b = (pixel >>> 8) & 0xFF;
        int a = pixel & 0xFF;
        if (a < 30 || (r < 38 && g < 38 && b < 38)) {
            visited[x][y] = true;
            q.add(new int[]{x, y});
        }
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        world.update(delta);

        Gdx.gl.glClearColor(0.08f, 0.09f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        drawFloors();
        drawBuildingsAndResources();
        drawPlayerSprite();
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawOverlaysAndPanelShapes();

        batch.begin();
        drawWorldLabelsAndUI();
        batch.end();
    }

    private void drawFloors() {
        Texture grassTex = textures.get("floor_grass.png");
        Texture factoryTex = textures.get("floor_factory.png");
        Texture mineTex = textures.get("floor_mine.png");

        for (int x = 0; x < GameWorld.GRID_COLS; x++) {
            for (int y = 0; y < GameWorld.GRID_ROWS; y++) {
                Tile tile = world.getMap().getTile(x, y);
                float px = x * Map.TILE_SIZE;
                float py = y * Map.TILE_SIZE;

                Texture floorToDraw;
                switch (tile.getZoneType()) {
                    case MINE:
                        // Раздел шахты (floor_mine.png, а если файла ещё нет — затемнённая порода)
                        if (mineTex != null) {
                            floorToDraw = mineTex;
                            batch.setColor(Color.WHITE);
                        } else {
                            floorToDraw = factoryTex != null ? factoryTex : grassTex;
                            batch.setColor(0.42f, 0.35f, 0.28f, 1f);
                        }
                        break;
                    case PRODUCTION:
                        // Выделенная красным зона цеха в бетоне (floor_factory.png)
                        floorToDraw = factoryTex;
                        batch.setColor(Color.WHITE);
                        break;
                    case SELL:
                        // Зона продажи на траве с ярким зелёным оттенком
                        floorToDraw = grassTex;
                        batch.setColor(0.55f, 1.0f, 0.60f, 1f);
                        break;
                    default:
                        // Всё остальное вокруг — в траве (floor_grass.png)
                        floorToDraw = grassTex;
                        batch.setColor(Color.WHITE);
                        break;
                }

                if (floorToDraw != null) {
                    batch.draw(floorToDraw, px, py, Map.TILE_SIZE - 1, Map.TILE_SIZE - 1);
                }
            }
        }
        batch.setColor(Color.WHITE);

        // Залежи железной руды в шахте
        Texture oreTex = textures.get(ResourceType.IRON_ORE.getTexturePath());
        if (oreTex != null) {
            for (int x = 1; x <= 4; x++) {
                for (int y = 11; y <= 14; y++) {
                    batch.draw(oreTex, x * Map.TILE_SIZE + 8, y * Map.TILE_SIZE + 8, Map.TILE_SIZE - 16, Map.TILE_SIZE - 16);
                }
            }
        }
    }

    private void drawBuildingsAndResources() {
        for (int x = 0; x < GameWorld.GRID_COLS; x++) {
            for (int y = 0; y < GameWorld.GRID_ROWS; y++) {
                Building b = world.getMap().getTile(x, y).getBuilding();
                if (b == null) continue;

                float px = x * Map.TILE_SIZE;
                float py = y * Map.TILE_SIZE;

                String texName = "conveyor.png";
                if (b instanceof Machine) {
                    texName = ((Machine) b).getTexturePath();
                }

                Texture bTex = textures.get(texName);
                if (bTex != null) {
                    if (b instanceof Conveyor) {
                        float angle = getConveyorRotationAngle(b.getDirection());
                        TextureRegion reg = new TextureRegion(bTex);
                        batch.draw(reg, px + 2, py + 2, (Map.TILE_SIZE - 4) / 2f, (Map.TILE_SIZE - 4) / 2f,
                                Map.TILE_SIZE - 4, Map.TILE_SIZE - 4, 1f, 1f, angle);
                    } else {
                        batch.draw(bTex, px + 2, py + 2, Map.TILE_SIZE - 4, Map.TILE_SIZE - 4);
                    }
                }
            }
        }

        // Ресурсы на конвейерах и в станках
        for (int x = 0; x < GameWorld.GRID_COLS; x++) {
            for (int y = 0; y < GameWorld.GRID_ROWS; y++) {
                Building b = world.getMap().getTile(x, y).getBuilding();
                if (b == null || b instanceof OreBox) continue;

                ResourceType res = b.getDisplayedResource();
                if (res != null) {
                    Texture rTex = textures.get(res.getTexturePath());
                    if (rTex != null) {
                        float rx = x * Map.TILE_SIZE + Map.TILE_SIZE / 2f;
                        float ry = y * Map.TILE_SIZE + Map.TILE_SIZE / 2f;

                        if (b instanceof Conveyor) {
                            float offset = (b.getProgressRatio() - 0.5f) * (Map.TILE_SIZE * 0.72f);
                            rx += b.getDirection().getDx() * offset;
                            ry += b.getDirection().getDy() * offset;
                        }
                        batch.draw(rTex, rx - 17, ry - 17, 34, 34);
                    }
                }
            }
        }
    }

    private float getConveyorRotationAngle(Direction dir) {
        switch (dir) {
            case UP: return 0f;
            case RIGHT: return -90f;
            case DOWN: return 180f;
            case LEFT: return 90f;
            default: return 0f;
        }
    }

    private void drawPlayerSprite() {
        Player p = world.getPlayer();
        String spriteName = "miner_up.png"; // По умолчанию стоит лицом к нам

        switch (p.getFacingDirection()) {
            case UP:
                spriteName = "miner_down.png"; // Идём вверх (от камеры) -> видим спину и рюкзак
                break;
            case DOWN:
                spriteName = "miner_up.png";   // Идём вниз (на камеру) -> видим лицо
                break;
            case LEFT:
                spriteName = "miner_left.png";
                break;
            case RIGHT:
                spriteName = "miner_right.png";
                break;
        }

        Texture pTex = textures.get(spriteName);
        if (pTex == null) pTex = textures.get("miner.png");

        if (pTex != null) {
            batch.draw(pTex, p.getX() - 28, p.getY() - 28, 56, 56);
        }
    }

    private void drawOverlaysAndPanelShapes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Верхняя полоса над картой (1024..1080)
        shapeRenderer.setColor(0.08f, 0.09f, 0.12f, 1f);
        shapeRenderer.rect(0, MAP_PIXEL_H, MAP_PIXEL_W, SCREEN_H - MAP_PIXEL_H);

        for (int x = 0; x < GameWorld.GRID_COLS; x++) {
            for (int y = 0; y < GameWorld.GRID_ROWS; y++) {
                Building b = world.getMap().getTile(x, y).getBuilding();
                if (b == null) continue;

                float px = x * Map.TILE_SIZE;
                float py = y * Map.TILE_SIZE;

                drawDirectionDot(px, py, b.getDirection(), Color.YELLOW);

                if (b instanceof Furnace) {
                    drawDirectionDot(px, py, ((Furnace) b).getSideDirection(), Color.ORANGE);
                }

                if (b instanceof Machine && !(b instanceof OreBox) && ((Machine) b).isProcessing()) {
                    float ratio = b.getProgressRatio();
                    shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
                    shapeRenderer.rect(px + 6, py + 4, Map.TILE_SIZE - 12, 7);
                    shapeRenderer.setColor(0.2f, 0.95f, 0.35f, 1f);
                    shapeRenderer.rect(px + 6, py + 4, (Map.TILE_SIZE - 12) * ratio, 7);
                }
            }
        }

        // Подсветка клетки под курсором мыши
        if (hoverGx >= 0 && hoverGy >= 0 && world.getMap().canBuildAt(hoverGx, hoverGy)) {
            float gx = hoverGx * Map.TILE_SIZE;
            float gy = hoverGy * Map.TILE_SIZE;
            shapeRenderer.setColor(1f, 1f, 1f, 0.25f);
            shapeRenderer.rect(gx + 2, gy + 2, Map.TILE_SIZE - 4, Map.TILE_SIZE - 4);
            drawDirectionDot(gx, gy, world.getBuildDirection(), Color.YELLOW);
        }

        // Полоска добычи руды над шахтёром
        Player p = world.getPlayer();
        if (p.getMiningProgress() > 0f) {
            float ratio = p.getMiningProgress() / world.getMine().getDeposit().getBaseMineTime();
            shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
            shapeRenderer.rect(p.getX() - 24, p.getY() + 30, 48, 8);
            shapeRenderer.setColor(0.2f, 0.9f, 1.0f, 1f);
            shapeRenderer.rect(p.getX() - 22, p.getY() + 32, 44 * Math.min(1f, ratio), 4);
        }

        // Правая панель управления (от x = 1344 до 1920)
        shapeRenderer.setColor(0.08f, 0.09f, 0.12f, 1f);
        shapeRenderer.rect(MAP_PIXEL_W, 0, SCREEN_W - MAP_PIXEL_W, SCREEN_H);

        MachineShop.CatalogItem[] items = MachineShop.CatalogItem.values();
        float btnTop = 835f;
        for (int i = 0; i < items.length; i++) {
            float by = btnTop - i * 38f;
            if (items[i] == world.getSelectedCatalogItem()) {
                shapeRenderer.setColor(0.25f, 0.36f, 0.18f, 1f);
            } else {
                shapeRenderer.setColor(0.14f, 0.16f, 0.21f, 1f);
            }
            shapeRenderer.rect(MAP_PIXEL_W + 16, by - 26f, SCREEN_W - MAP_PIXEL_W - 32, 34f);
        }

        float upgTop = 400f;
        for (int i = 0; i < 3; i++) {
            float by = upgTop - i * 38f;
            shapeRenderer.setColor(0.15f, 0.20f, 0.28f, 1f);
            shapeRenderer.rect(MAP_PIXEL_W + 16, by - 26f, SCREEN_W - MAP_PIXEL_W - 32, 34f);
        }

        shapeRenderer.end();
    }

    private void drawDirectionDot(float px, float py, Direction dir, Color color) {
        shapeRenderer.setColor(color);
        float cx = px + Map.TILE_SIZE / 2f + dir.getDx() * 22f;
        float cy = py + Map.TILE_SIZE / 2f + dir.getDy() * 22f;
        shapeRenderer.circle(cx, cy, 5.0f);
    }

    private void drawWorldLabelsAndUI() {
        font.setColor(Color.ORANGE);
        font.draw(batch, "Шахта", 1 * Map.TILE_SIZE + 4, 15 * Map.TILE_SIZE + 22);

        font.setColor(Color.CYAN);
        font.draw(batch, "Зона для автоматизації", 6 * Map.TILE_SIZE + 8, 12 * Map.TILE_SIZE + 22);

        font.setColor(Color.GREEN);
        font.draw(batch, "Зона продажу", 1 * Map.TILE_SIZE + 4, 5 * Map.TILE_SIZE + 22);

        font.setColor(Color.WHITE);

        for (int x = 0; x < GameWorld.GRID_COLS; x++) {
            for (int y = 0; y < GameWorld.GRID_ROWS; y++) {
                Building b = world.getMap().getTile(x, y).getBuilding();
                if (b instanceof OreBox) {
                    OreBox box = (OreBox) b;
                    font.setColor(Color.YELLOW);
                    font.draw(batch, "Руди: " + box.getStoredOreCount(),
                            x * Map.TILE_SIZE + 2, (y + 1) * Map.TILE_SIZE + 20);
                }
            }
        }

        float x = MAP_PIXEL_W + 24;
        float y = 1050;
        font.setColor(Color.GOLD);
        y -= 34;

        font.setColor(Color.GREEN);
        font.draw(batch, "Баланс: $" + world.getMoneyManager().getBalance(), x, y);
        y -= 28;

        Player p = world.getPlayer();
        font.setColor(Color.WHITE);
        font.draw(batch, "Інвентар: " + p.getBackpack().getInventory().getTotalCount() + " / " + p.getBackpack().getCapacity(), x, y);
        y -= 28;
        font.draw(batch, "Кирка: " + p.getPickaxe().getName(), x, y);
        y -= 28;
        font.draw(batch, "Розташування: " + world.getBuildDirection().getLabel(), x, y);

        font.setColor(Color.CYAN);
        font.draw(batch, "Магазин механізмів", x, 875);

        MachineShop.CatalogItem[] items = MachineShop.CatalogItem.values();
        float btnTop = 835f;
        MachineShop.CatalogItem hoveredShopItem = null; // Для відстеження підказки

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        for (int i = 0; i < items.length; i++) {
            float by = btnTop - i * 38f;
            Texture icon = textures.get(items[i].getTextureName());
            if (icon != null) {
                batch.draw(icon, x + 6, by - 22f, 26, 26);
            }
            if (items[i] == world.getSelectedCatalogItem()) font.setColor(Color.YELLOW);
            else font.setColor(Color.WHITE);
            String keyLabel = (i == 9) ? "0" : String.valueOf(i + 1);
            font.draw(batch, "[" + keyLabel + "] " + items[i].getTitle() + " ($" + items[i].getCost() + ")", x + 40, by - 2);

            // Перевіряємо, чи курсор миші знаходиться над цією кнопкою
            if (mouse.x >= MAP_PIXEL_W + 16 && mouse.x <= SCREEN_W - 16 && mouse.y >= by - 26f && mouse.y <= by + 8f) {
                hoveredShopItem = items[i];
            }
        }

        font.setColor(Color.CYAN);
        font.draw(batch, "Модернізація (Кнопки Z,X,C)", x, 440);

        float upgTop = 400f;
        font.setColor(Color.WHITE);
        String bpCost = p.getBackpack().canUpgrade() ? "$" + world.getUpgradeShop().getBackpackUpgradeCost(p) : "MAX";
        font.draw(batch, "[Z] Ранець (" + p.getBackpack().getCapacity() + "): " + bpCost, x + 10, upgTop - 2);

        String pkCost = p.getPickaxe().canUpgrade() ? "$" + world.getUpgradeShop().getPickaxeUpgradeCost(p) : "MAX";
        font.draw(batch, "[X] Кирка Lvl " + (p.getPickaxe().getLevel() + 1) + ": " + pkCost, x + 10, upgTop - 40);

        String spCost = p.getUpgrades().canUpgradeWorkSpeed() ? "$" + world.getUpgradeShop().getWorkSpeedUpgradeCost(p) : "MAX";
        font.draw(batch, "[C] Швидкість копання " + (p.getUpgrades().getWorkSpeedLevel() + 1) + ": " + spCost, x + 10, upgTop - 78);

        float invY = 265f;
        font.setColor(Color.CYAN);
        font.draw(batch, "Інвентар:", x, invY);
        invY -= 28;
        font.setColor(Color.LIGHT_GRAY);
        boolean empty = true;
        for (ResourceType type : ResourceType.values()) {
            ResourceStack stack = p.getBackpack().getInventory().getStacks().get(type);
            if (stack != null && stack.getAmount() > 0) {
                empty = false;
                Texture rIcon = textures.get(type.getTexturePath());
                if (rIcon != null) {
                    batch.draw(rIcon, x + 4, invY - 20f, 24, 24);
                }
                font.draw(batch, type.getDisplayName() + ": " + stack.getAmount() + " ($" + type.getBasePrice() + ")", x + 36, invY);
                invY -= 26;
            }
        }
        if (empty) {
            font.draw(batch, "(Порожньо. Для копання перейдіть в зону шахти)", x, invY);
        }

        if (hoveredBuilding != null) {
            font.setColor(Color.YELLOW);
            String extra = (hoveredBuilding instanceof OreBox)
                    ? "У бункері: " + ((OreBox) hoveredBuilding).getStoredOreCount()
                    : "Усередині: " + (hoveredBuilding.getDisplayedResource() != null ? hoveredBuilding.getDisplayedResource().getDisplayName() : "Порожньо");
            font.draw(batch, "Огляд: " + hoveredBuilding.getName() + " | " + extra + " | Вихід: " + hoveredBuilding.getDirection().getLabel(), 20, 56);
        }
        font.setColor(Color.WHITE);

        // Відмальовка спливаючої підказки (Tooltip)
        if (hoveredShopItem != null) {
            batch.end(); // Призупиняємо малювання тексту, щоб намалювати фон підказки

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 0.9f); // Темно-синій напівпрозорий фон
            shapeRenderer.rect(mouse.x - 220, mouse.y - 70, 210, 60);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin(); // Продовжуємо малювати текст
            font.setColor(Color.YELLOW);
            font.draw(batch, hoveredShopItem.getDescription(), mouse.x - 210, mouse.y - 20);
        }
    }

    private void handleInput(float dt) {
        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 1;

        world.getPlayer().move(dx, dy, dt, MAP_PIXEL_W, MAP_PIXEL_H);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.rotateBuildDirection();
        }

        MachineShop.CatalogItem[] items = MachineShop.CatalogItem.values();
        for (int i = 0; i < items.length && i < 9; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                world.setSelectedCatalogItem(items[i]);
            }
        }
        if (items.length >= 10 && Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            world.setSelectedCatalogItem(items[9]);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) triggerBackpackUpgrade();
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) triggerPickaxeUpgrade();
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) triggerSpeedUpgrade();

        Tile pTile = world.getMap().getTile(world.getPlayer().getGridX(), world.getPlayer().getGridY());
        if (pTile != null && pTile.getZoneType() == Tile.ZoneType.MINE
                && (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.E))) {
            if (world.getPlayer().mineManually(dt, world.getMine())) {
                world.setStatusMessage("Додано 1 металеву руду! Продати у зоні продажу або викласти у бокс с ресурсами");
            }
        } else {
            world.getPlayer().resetMiningProgress();
        }

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        if (mouse.x >= MAP_PIXEL_W && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            handleShopPanelClick(mouse.x, mouse.y);
            return;
        }

        hoverGx = (int) (mouse.x / Map.TILE_SIZE);
        hoverGy = (int) (mouse.y / Map.TILE_SIZE);

        if (world.getMap().isValid(hoverGx, hoverGy) && mouse.x < MAP_PIXEL_W && mouse.y < MAP_PIXEL_H) {
            hoveredBuilding = world.getMap().getTile(hoverGx, hoverGy).getBuilding();
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                world.placeOrRotateBuilding(hoverGx, hoverGy);
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                world.removeBuilding(hoverGx, hoverGy);
            }
        } else {
            hoveredBuilding = null;
            hoverGx = -1;
            hoverGy = -1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            world.tryInteractNearbyOrHovered(hoveredBuilding);
        }
    }

    private void handleShopPanelClick(float mx, float my) {
        MachineShop.CatalogItem[] items = MachineShop.CatalogItem.values();
        float btnTop = 835f;
        for (int i = 0; i < items.length; i++) {
            float by = btnTop - i * 38f;
            if (mx >= MAP_PIXEL_W + 16 && mx <= SCREEN_W - 16 && my >= by - 26f && my <= by + 8f) {
                world.setSelectedCatalogItem(items[i]);
                world.setStatusMessage("Selected: " + items[i].getTitle() + " ($" + items[i].getCost() + ")");
                return;
            }
        }

        float upgTop = 400f;
        if (mx >= MAP_PIXEL_W + 16 && mx <= SCREEN_W - 16) {
            if (my >= upgTop - 26f && my <= upgTop + 8f) triggerBackpackUpgrade();
            else if (my >= upgTop - 64f && my <= upgTop - 30f) triggerPickaxeUpgrade();
            else if (my >= upgTop - 102f && my <= upgTop - 68f) triggerSpeedUpgrade();
        }
    }

    private void triggerBackpackUpgrade() {
        if (world.getUpgradeShop().buyBackpackUpgrade(world.getPlayer())) {
            world.setStatusMessage("Ранець продвинуто на " + world.getPlayer().getBackpack().getCapacity() + " слотів!");
        } else {
            world.setStatusMessage("Не вистачає грошей на модернізацію ранцю.");
        }
    }

    private void triggerPickaxeUpgrade() {
        if (world.getUpgradeShop().buyPickaxeUpgrade(world.getPlayer())) {
            world.setStatusMessage("Продвинуто до" + world.getPlayer().getPickaxe().getName() + "!");
        } else {
            world.setStatusMessage("Не вистачає грошей на модернізацію кирки.");
        }
    }

    private void triggerSpeedUpgrade() {
        if (world.getUpgradeShop().buyWorkSpeedUpgrade(world.getPlayer())) {
            world.setStatusMessage("Швидкість копання було підвищено." + (world.getPlayer().getUpgrades().getWorkSpeedLevel() + 1) + "!");
        } else {
            world.setStatusMessage("Не вистачає грошей на модернізацію швидкості видобування.");
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        for (Texture t : textures.values()) t.dispose();
    }
}