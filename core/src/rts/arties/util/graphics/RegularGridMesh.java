/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.util.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import rts.arties.util.graphics.mesh.IntMesh;
import rts.arties.util.graphics.shader.ExtShaderProgram;

/**
 * A regular grid mesh
 */
public class RegularGridMesh {
    private static final int T_SIZE = 6 * 3;
    private Mesh mesh;
    private int w, h;

    private float[] verts;
    //private short[] indices;

    public RegularGridMesh(float x, float y, float cellWidth, float cellHeight, int w, int h) {
        this.w = w;
        this.h = h;
        this.mesh = createRegularGridMesh(x, y, cellWidth, cellHeight, w, h);
    }

    public void dispose() {
        mesh.dispose();
    }

    /** Renders the quad with the specified shader program. */
    public void render(ShaderProgram program) {
        mesh.render(program, GL20.GL_TRIANGLES, 0, w * h * 6);
    }

    private Mesh createRegularGridMesh(float x, float y, float cellWidth, float cellHeight, int w, int h) {
        verts = new float[w * h * 3 * 6];
        //indices = new short[w * h * 6];

        // Genreate vx0, vy0, c0, vx1, vy1, c1...
        float black = Color.toFloatBits(0f, 0f, 0f, 1f);

        int k = 0;
        for (int j = 0; j < this.h; j++) {
            for (int i = 0; i < this.w; i++) {

                // V00
                verts[k + 0] = x + i * cellWidth;
                verts[k + 1] = y + j * cellHeight;
                verts[k + 2] = black;
                // V10
                verts[k + 3] = x + (i + 1) * cellWidth;
                verts[k + 4] = y + j * cellHeight;
                verts[k + 5] = black;
                // V11
                verts[k + 6] = x + (i + 1) * cellWidth;
                verts[k + 7] = y + (j + 1) * cellHeight;
                verts[k + 8] = black;

                // V00
                verts[k + 9] = x + i * cellWidth;
                verts[k + 10] = y + j * cellHeight;
                verts[k + 11] = black;
                // V11
                verts[k + 12] = x + (i + 1) * cellWidth;
                verts[k + 13] = y + (j + 1) * cellHeight;
                verts[k + 14] = black;
                // V01
                verts[k + 15] = x + i * cellWidth;
                verts[k + 16] = y + (j + 1) * cellHeight;
                verts[k + 17] = black;

                k += T_SIZE;
            }
        }
        // Mesh with position and color
        Mesh mesh = new Mesh(false, this.w * this.h * 6, 0, new VertexAttribute(Usage.Position, 2, ExtShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.ColorPacked, 4, ExtShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(verts);
        return mesh;
    }

    public void setColor(int i, int j, float color){
        int offset = i * T_SIZE + j * w * T_SIZE;
        if(offset >= 0 && offset < verts.length - T_SIZE) {
            verts[offset + 2] = color;
            verts[offset + 5] = color;
            verts[offset + 8] = color;
            verts[offset + 11] = color;
            verts[offset + 14] = color;
            verts[offset + 17] = color;
            mesh.updateVertices(offset, verts, offset, T_SIZE);
        }
    }

}
