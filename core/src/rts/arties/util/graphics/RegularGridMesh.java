/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.util.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.util.graphics.mesh.IntMesh;
import rts.arties.util.graphics.shader.ExtShaderProgram;

/**
 * A regular grid mesh
 */
public class RegularGridMesh {
    private static final int V_SIZE = 3;
    private static final int I_SIZE = 6;
    private IntMesh mesh;
    private int cellsW, cellsH;

    private float[] verts;
    private int[] indices;

    public RegularGridMesh(float x, float y, float cellWidth, float cellHeight, int cellsW, int cellsH) {
        this.cellsW = cellsW;
        this.cellsH = cellsH;
        this.mesh = createRegularGridMesh(x, y, cellWidth, cellHeight, cellsW, cellsH);
    }

    public void dispose() {
        mesh.dispose();
    }

    /** Renders the quad with the specified shader program. */
    public void render(ExtShaderProgram program) {
        mesh.render(program, GL20.GL_TRIANGLES, 0, indices.length);
    }

    private IntMesh createRegularGridMesh(float x, float y, float cellWidth, float cellHeight, int w, int h) {
        verts = new float[(w + 1) * (h + 1) * V_SIZE];
        indices = new int[w * h * I_SIZE];

        // Generate x0, y0, c0, x1, y1, c1...
        float black = Color.toFloatBits(0f, 0f, 0f, 1f);

        // Generate vertices
        int k = 0;
        for (int j = 0; j < h + 1; j++) {
            for (int i = 0; i < w + 1; i++) {
                // Position
                verts[k + 0] = x + i * cellWidth;
                verts[k + 1] = y + j * cellHeight;
                // Color
                verts[k + 2] = black;

                k += V_SIZE;
            }
        }
        // Generate quad indices
        k = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                // We create the quads from the bottom-left position (00)
                int i00 = (i + j * (w + 1));
                int i10 = ((i + 1) + j * (w + 1));
                int i11 = ((i + 1) + (j + 1) * (w + 1));
                int i01 = (i + (j + 1) * (w + 1));
                indices[k + 0] = i00;
                indices[k + 1] = i10;
                indices[k + 2] = i11;
                indices[k + 3] = i00;
                indices[k + 4] = i11;
                indices[k + 5] = i01;

                k += I_SIZE;
            }
        }

        // Mesh with position and color
        IntMesh mesh = new IntMesh(true, verts.length, indices.length, new VertexAttribute(Usage.Position, 2, ExtShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.ColorPacked, 4, ExtShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(verts);
        mesh.setIndices(indices);
        return mesh;
    }

    public void setColor(int i, int j, float color) {
        int cws = (cellsW + 1) * V_SIZE;
        int i00 = i * V_SIZE + j * cws;
        int i10 = (i + 1) * V_SIZE + j * cws;
        int i11 = (i + 1) * V_SIZE + (j + 1) * cws;
        int i01 = i * V_SIZE + (j + 1) * cws;
        verts[i00 + 2] = color;
        verts[i10 + 2] = color;
        verts[i11 + 2] = color;
        verts[i01 + 2] = color;
        mesh.updateVertices(i00 + 2, verts, i00 + 2, 4);
        mesh.updateVertices(i01 + 2, verts, i01 + 2, 4);
    }

}
