package gpjecc.blogspot.com.network.world;

// Cópia da classe Rectangle do LibGDX, para o servidor poder usá-la.
public class ServerRectangle {
    public float x, y, width, height;

    public ServerRectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean overlaps(ServerRectangle r) {
        return this.x < r.x + r.width &&
               this.x + this.width > r.x &&
               this.y < r.y + r.height &&
               this.y + this.height > r.y;
    }
}