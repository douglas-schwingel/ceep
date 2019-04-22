package br.com.alura.ceep.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import br.com.alura.ceep.model.Nota;

public class NotaDAOSQL extends SQLiteOpenHelper {

    private Context context;
    private static ArrayList<Nota> notas;

    public NotaDAOSQL(@Nullable Context context) {
        super(context, "Notas", null, 2);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Notas (id INTEGER PRIMARY KEY, " +
                "titulo TEXT, " +
                "descricao TEXT, " +
                "ordem INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "";
        switch (oldVersion) {
            case 1:
                sql = "ALTER TABLE Notas ADD COLUMN ordem INTEGER";
                db.execSQL(sql);
                break;
        }
    }

    public ArrayList<Nota> buscaNotas() {
        String sql = "SELECT * from Notas";
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor c = rdb.rawQuery(sql, null);

        notas = new ArrayList<>();
        while(c.moveToNext()) {
            Integer id = c.getInt(c.getColumnIndex("id"));
            String titulo = c.getString(c.getColumnIndex("titulo"));
            String descricao = c.getString(c.getColumnIndex("descricao"));
            Integer ordem = c.getInt(c.getColumnIndex("ordem"));
            Nota nota = new Nota(titulo, descricao);
            nota.setId(id.longValue());
            nota.setOrdem(ordem.longValue());
            notas.add(ordem, nota);
        }

        c.close();

        return notas;
    }

    public void insere(Nota nota) {
        SQLiteDatabase wdb = getWritableDatabase();
        ContentValues dados = getContentValues(nota);
        dados.put("ordem", notas.size());

        wdb.insert("Notas", null, dados);
    }

    private ContentValues getContentValues(Nota nota) {
        ContentValues dados = new ContentValues();
        dados.put("titulo", nota.getTitulo());
        dados.put("descricao", nota.getDescricao());
        return dados;
    }

    public void altera(Nota nota, int posicao) {
        SQLiteDatabase wdb = getWritableDatabase();
        ContentValues dados = getContentValues(nota);

        String[] params = {Integer.toString(posicao)};
        wdb.update("Notas", dados, "ordem = ?", params);
    }

    public void deleta(int id) {
        SQLiteDatabase wdb = getWritableDatabase();
        String[] params = {Integer.toString(id)};
        wdb.delete("Notas", "id = ?", params);
    }

    public void troca(int posicaoInicial, int posicaoFinal) {
        SQLiteDatabase wdbInicial = getWritableDatabase();
        ContentValues dadosInicial = new ContentValues();
        dadosInicial.put("ordem", posicaoFinal);
        String[] paramIncial = {String.valueOf(notas.get(posicaoInicial).getId())};
        wdbInicial.update("Notas", dadosInicial, "id = ?", paramIncial);

        SQLiteDatabase wdbFinal = getWritableDatabase();
        ContentValues dadosFinal = new ContentValues();
        dadosFinal.put("ordem", posicaoInicial);
        String[] paramFinal = {String.valueOf(notas.get(posicaoFinal).getId())};
        wdbFinal.update("Notas", dadosFinal, "id = ?", paramFinal);
    }
}
