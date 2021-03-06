package com.proyecto.univalle.proyectomoviles;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView lblUser;
    private TextView lblCorreo;
    private ImageView imgFoto;
    GoogleMap gp;
    FragmentManager manager;
    String nombre;
    String correo;
    String foto;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //con esto generamos el usuario en el header del menu-------------------------------
        View hView = navigationView.getHeaderView(0);
        lblUser = (TextView) hView.findViewById(R.id.lblUser);
        lblCorreo = (TextView) hView.findViewById(R.id.lblCorreo);
        imgFoto = (ImageView) hView.findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        //----------------------------------------------------------------------------------
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //nombre=""+currentUser.getDisplayName();
            correo=""+currentUser.getEmail();
            lblCorreo.setText(correo);
        }

        Bundle recibo = getIntent().getExtras();
        if (recibo != null) {
            nombre = recibo.getString("nombre");
            correo = recibo.getString("correo");
            foto = recibo.getString("foto");
            lblUser.setText(nombre);
            lblCorreo.setText(correo);
            new LoadImage(imgFoto).execute(foto);
        }

        ///Publicidad
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
        });

        //crearFragmentMapa();

        //readJson();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            //Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_acerca) {
            //Toast.makeText(this, "Elaborado por", Toast.LENGTH_SHORT).show();
            boolean showMessage = false;
            String message="";
            message="Desarrollado por:\n\nOscar Alexander Ruiz Palacio\nAndrés Felipe Medina Tascón\nJhon Henry Gutiérrez Bustos\nAndrés Felipe González Rojas\n\nUniversidad del Valle - Sede Tuluá";
            showMessage=true;
            if(showMessage) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage(message);
                alertDialog.show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void verPerfil(View g) {
        Intent ir = new Intent(MainActivity.this, PerfilActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);

        if (!(lblCorreo.getText().equals("Invitado"))) {
            Bundle bundle = new Bundle();
            bundle.putString("nombre", nombre);
            bundle.putString("correo", correo);
            bundle.putString("foto", foto);
            ir.putExtras(bundle);
        }
        startActivity(ir);
    }

    /*public void crearFragmentMapa() {

        if (gp instanceof GoogleMap) {

        } else {
            gp = new GoogleMap();
            android.support.v4.app.FragmentTransaction transtion = getSupportFragmentManager().beginTransaction();
            transtion.add(R.id.fragment, gp);
            transtion.commit();
        }
    }*/

    public void readJson() {
        String jsonString = IOHelper.stringFromAsset(this, "parques.json");
        try {
            JSONArray parques = new JSONArray(jsonString);

            String result = "";
            for (int i = 0; i < parques.length(); i++) {
                JSONObject parks = parques.getJSONObject(i);

                String direccion = parks.getString("direcci_n");
                String georeferencia = parks.getString("georeferenciaci_n");
                String nombre = parks.getString("nombre_del_parque");
                System.out.println("Dato #" + i + ": " + direccion + " " + georeferencia + " " + nombre);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n");
        jsonString = IOHelper.stringFromAsset(this, "iglesias.json");
        try {
            JSONArray iglesias = new JSONArray(jsonString);

            String result = "";
            for (int i = 0; i < iglesias.length(); i++) {
                JSONObject parks = iglesias.getJSONObject(i);
                String barrio = parks.getString("barrio");
                String direccion = parks.getString("direcci_n");
                String parroquia = parks.getString("parroquia");
                String georeferencia = parks.getString("georeferencia");
                System.out.println("Dato #" + i + ": " + barrio + " " + direccion + " " + parroquia + " " + georeferencia);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verServicioPublico(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 2);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verEmpresasTransporte(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 3);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verBancos(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 4);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verIglesias(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 7);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verDroguerias(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 1);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verParques(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 6);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verSupermercados(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 5);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void verComidas(View v){
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", 8);
        ir.putExtras(bundle);
        startActivity(ir);
    }

    public void irInfoCaterogias(int tipoCat) {
        Intent ir = new Intent(MainActivity.this, InfoCategoriasActivity.class);
        ir.addFlags(ir.FLAG_ACTIVITY_CLEAR_TOP | ir.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("Tipo", tipoCat);
        ir.putExtras(bundle);
        startActivity(ir);
    }

}
