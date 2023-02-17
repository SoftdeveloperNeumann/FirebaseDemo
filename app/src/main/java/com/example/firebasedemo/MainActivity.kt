package com.example.firebasedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasedemo.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

data class User(val name:String="", val first: String="", val value:String="")

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var db = FirebaseDatabase.getInstance()
    lateinit var messageRef: DatabaseReference
    lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageRef = db.getReference("message")
        userRef = db.getReference("user")


        binding.btnSend.setOnClickListener {
//            messageRef.setValue(binding.etTextInput.text.toString()) // für einfache Werte

            // als Child in der DB ablegen
//            val map = hashMapOf(
//                "name" to "Neumann",
//                "first" to "Frank",
//                "value" to binding.etTextInput.text.toString()
//            )
//            messageRef.setValue(map)
//            messageRef.push().setValue(map)

            val user = User("Neumann","Frank", binding.etTextInput.text.toString())
            val userID = userRef.push().key
//            userRef.child(userID!!).setValue(user)
            userRef.child("Frank Neumann").setValue(user)
            Log.d("TAG", "onCreate: $userID")
        }

        binding.btnGetUser.setOnClickListener {
            val userTask = userRef.child("Frank Neumann").get()
                .addOnSuccessListener {
                    val user = it.getValue(User::class.java)
                    binding.tvOutput.text = "$user"
                }

        }


        // Listener für einfache Werte
//        messageRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//              val value = snapshot.value as String
//                binding.tvOutput.text = value
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@MainActivity, "Fehler beim Lesen der Daten", Toast.LENGTH_SHORT).show()
//            }
//        })

        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               val map = snapshot.getValue() as HashMap<String,String>
                binding.tvOutput.text = map["name"]
                binding.tvOutput.append(" schreibt: ${map["value"]}")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                binding.tvOutput.text = "Der neueste User hat geschrieben ${user?.value}"
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                binding.tvOutput.text = "User ${snapshot.key} hat den Eintrag in ${user?.value} geändert"
                Log.d("TAG", "onChildChanged: $previousChildName")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}