package livrokotlin.com.br.listaparacompras

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.db.delete
import java.text.NumberFormat
import java.util.*
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val adapter = ProdutoAdapter(this)

        //definindo o adaptador na lista
        list_view_produtos.adapter = adapter

        list_view_produtos.setOnItemClickListener{
                _: AdapterView<*>, view1: View, i: Int, l: Long ->




            alert("Mensagem", "Titulo"){
                //Botão de OK
                yesButton {
                    //Ação caso escolheu a opção SIM
                }

                //Botão de calcel
                noButton {
                    //Ação caso escolheu a opção NAO
                }

            }.show()
        }

        list_view_produtos.setOnItemLongClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->

            val opcoes = listOf("editar", "excluir")

            val opc_editar = 0;
            val opc_excluir = 1;

            selector("O que deseja fazer?", opcoes, { dialogInterface, position ->


                when (position){

                    opc_editar -> {

                        alert("Editar").show()
                        toast("Editar")
                    }

                    opc_excluir ->{

                        //buscando o item clicado
                        val item = adapter.getItem(i)

                        //removendo o item clicado da lista
                        adapter.remove(item)

                        //deletando do banco de dados
                        deletarProduto(item!!.id)

                        toast("item deletado com sucesso")

                    }
                }


            })


            true
        }

        btn_adicionar.setOnClickListener {

            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)

        }

    }



    fun deletarProduto(idProduto:Int) {

        database.use {


            delete("produtos", "id = {id}", "id" to idProduto)


        }

    }

    override fun onResume() {
        super.onResume()

        val adapter = list_view_produtos.adapter as ProdutoAdapter

        database.use{

            select("produtos").exec {

                val parser = rowParser {

                        id: Int, nome: String,
                        quantidade: Int,
                        valor:Double,
                        foto:ByteArray? ->
                    //Colunas do banco de dados


                    //Montagem do objeto Produto com as colunas do banco
                    Produto(id, nome, quantidade, valor, foto?.toBitmap() )
                }


                var listaProdutos = parseList(parser)


                adapter.clear()
                adapter.addAll(listaProdutos)


                val soma = listaProdutos.sumByDouble { it.valor * it.quantidade }

                val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))
                txt_total.text = "TOTAL: ${ f.format(soma)}"

            }

        }

    }

}