package com.banklannister.xogames.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.banklannister.xogames.data.GameState
import com.banklannister.xogames.data.GamesThree
import com.banklannister.xogames.databinding.ActivityThreeTableBinding
import com.banklannister.xogames.models.GamesThreeData

class ThreeTableActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding: ActivityThreeTableBinding

    private var gamesThreeData : GamesThreeData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GamesThree.fetchGameDataThree()

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.readyButton.setOnClickListener {
            startGame()
        }

        GamesThree.gameModel.observe(this){
            gamesThreeData = it
            setUI()
        }




    }

    private fun setUI(){
        gamesThreeData?.apply {
            binding.btn0.text = position[0]
            binding.btn1.text = position[1]
            binding.btn2.text = position[2]
            binding.btn3.text = position[3]
            binding.btn4.text = position[4]
            binding.btn5.text = position[5]
            binding.btn6.text = position[6]
            binding.btn7.text = position[7]
            binding.btn8.text = position[8]

            binding.readyButton.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when(gameStatus){
                    GameState.CREATED -> {
                        binding.readyButton.visibility = View.VISIBLE
                        "MATCH ID :$matchId"
                    }
                    GameState.INPROGRESS ->{
                        binding.readyButton.visibility = View.VISIBLE
                        when(GamesThree.myID){
                            currentPlayer -> "Your turn"
                            else -> "$currentPlayer turn"
                        }

                    }
                    GameState.FINISHED ->{
                        if(winner.isNotEmpty()) {
                            when(GamesThree.myID){
                                winner -> "You won"
                                else -> "$winner Won"
                            }

                        }
                        else "DRAW"
                    }
                }

        }
    }


    private fun startGame(){
        gamesThreeData?.apply {
            updateGameData(
                GamesThreeData(
                    matchId = matchId,
                    gameStatus = GameState.INPROGRESS
                )
            )
        }
    }

    private fun updateGameData(model : GamesThreeData){
        GamesThree.saveGameThree(model)
    }

   private fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )

        gamesThreeData?.apply {
            for ( i in winningPos){
                if(
                    position[i[0]] == position[i[1]] &&
                    position[i[1]]== position[i[2]] &&
                    position[i[0]].isNotEmpty()
                ){
                    gameStatus = GameState.FINISHED
                    winner = position[i[0]]
                }
            }

            if( position.none(){ it.isEmpty() }){
                gameStatus = GameState.FINISHED
            }

            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gamesThreeData?.apply {
            if(gameStatus!= GameState.INPROGRESS){
                Toast.makeText(applicationContext,"PRESS READY BUTTON",Toast.LENGTH_SHORT).show()
                return
            }

            val clickedPosition =(v?.tag  as String).toInt()
            if(position[clickedPosition].isEmpty()){
                position[clickedPosition] = currentPlayer
                currentPlayer = if(currentPlayer=="X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }

        }
    }
}