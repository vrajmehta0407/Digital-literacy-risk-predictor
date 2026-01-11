package com.dlrp.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dlrp.app.R
import com.dlrp.app.auth.UserManager
import com.dlrp.app.premium.PremiumFeatures

class PremiumActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var tvTrialStatus: TextView
    private lateinit var btnActivatePremium: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Premium Features"

        userManager = UserManager(this)
        
        initializeViews()
        loadPremiumStatus()
        setupFeaturesList()
    }

    private fun initializeViews() {
        tvTrialStatus = findViewById(R.id.tvTrialStatus)
        btnActivatePremium = findViewById(R.id.btnActivatePremium)
        recyclerView = findViewById(R.id.recyclerViewFeatures)
    }

    private fun loadPremiumStatus() {
        val user = userManager.getCurrentUser()
        
        if (user != null) {
            when {
                user.isPremiumActive() -> {
                    if (user.isTrialActive()) {
                        tvTrialStatus.text = "🎉 Free Trial Active!"
                        btnActivatePremium.text = "Upgrade to Premium (₹500/year)"
                    } else {
                        tvTrialStatus.text = "⭐ Premium Active"
                        btnActivatePremium.visibility = View.GONE
                    }
                }
                else -> {
                    tvTrialStatus.text = "Trial Expired - Upgrade to Premium"
                    btnActivatePremium.text = "Activate Premium (₹500/year)"
                }
            }
        }

        btnActivatePremium.setOnClickListener {
            Toast.makeText(this, "Payment integration coming soon! Contact support to activate premium.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupFeaturesList() {
        val features = PremiumFeatures.getPremiumFeatures()
        val adapter = FeaturesAdapter(features)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class FeaturesAdapter(private val features: List<PremiumFeatures.Feature>) :
        RecyclerView.Adapter<FeaturesAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvFeatureName: TextView = view.findViewById(R.id.tvFeatureName)
            val tvFeatureDesc: TextView = view.findViewById(R.id.tvFeatureDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_premium_feature, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val feature = features[position]
            holder.tvFeatureName.text = "⭐ ${feature.name.replace("_", " ")}"
            holder.tvFeatureDesc.text = PremiumFeatures.getFeatureDescription(feature)
        }

        override fun getItemCount() = features.size
    }
}
