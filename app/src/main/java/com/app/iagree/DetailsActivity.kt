package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.bottomsheet_select_course.view.*
import kotlinx.android.synthetic.main.bottomsheet_select_degree.view.*
import kotlinx.android.synthetic.main.bottomsheet_select_university.*
import kotlinx.android.synthetic.main.bottomsheet_select_university.view.*
import kotlinx.android.synthetic.main.bottomsheet_select_university.view.rtuImg

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val university = findViewById<LinearLayout>(R.id.l1)
        val degree = findViewById<LinearLayout>(R.id.l2)
        val programme = findViewById<LinearLayout>(R.id.l3)

        val bottomSheetDialogUniversity = BottomSheetDialog(this)
        val v1 = layoutInflater.inflate(R.layout.bottomsheet_select_university,null)
        bottomSheetDialogUniversity.setContentView(v1)

        checkCondition()

        v1.rtu.setOnClickListener {
            v1.rtuImg.visibility = View.VISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "Riga Technical University"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }


        v1.isma.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.VISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "ISMA University"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.ul.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.VISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "University of Latvia"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.tu.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.VISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "Turiba University"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.rsu.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.VISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "Riga Stradiņš University"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.riseba.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.VISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "RISEBA University of Applied Science"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.eka.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.VISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "EKA University of Applied Sciences"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.liepaja.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.VISIBLE
            v1.bankuImg.visibility = View.INVISIBLE
            textUniversity.text = "Liepaja University"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        v1.banku.setOnClickListener {
            v1.rtuImg.visibility = View.INVISIBLE
            v1.ismaImg.visibility = View.INVISIBLE
            v1.ulImg.visibility = View.INVISIBLE
            v1.tuImg.visibility = View.INVISIBLE
            v1.rsuImg.visibility = View.INVISIBLE
            v1.risebaImg.visibility = View.INVISIBLE
            v1.ekaImg.visibility = View.INVISIBLE
            v1.liepajaImg.visibility = View.INVISIBLE
            v1.bankuImg.visibility = View.VISIBLE
            textUniversity.text = "BA School of Business and Finance"
            bottomSheetDialogUniversity.dismiss()
            checkCondition()
        }

        val bottomSheetDialogDegree = BottomSheetDialog(this)
        val v2 = layoutInflater.inflate(R.layout.bottomsheet_select_degree,null)
        bottomSheetDialogDegree.setContentView(v2)

        v2.bachelors.setOnClickListener {
            v2.bachelorsImg.visibility = View.VISIBLE
            v2.mastersImg.visibility = View.INVISIBLE
            v2.phdImg.visibility = View.INVISIBLE
            textDegree.text = "Bachelors"
            bottomSheetDialogDegree.dismiss()
            checkCondition()
        }

        v2.masters.setOnClickListener {
            v2.bachelorsImg.visibility = View.INVISIBLE
            v2.mastersImg.visibility = View.VISIBLE
            v2.phdImg.visibility = View.INVISIBLE
            textDegree.text = "Masters"
            bottomSheetDialogDegree.dismiss()
            checkCondition()
        }

        v2.phd.setOnClickListener {
            v2.bachelorsImg.visibility = View.INVISIBLE
            v2.mastersImg.visibility = View.INVISIBLE
            v2.phdImg.visibility = View.VISIBLE
            textDegree.text = "Doctoral"
            bottomSheetDialogDegree.dismiss()
            checkCondition()
        }

        val bottomSheetDialogProgramme = BottomSheetDialog(this)
        val v3 = layoutInflater.inflate(R.layout.bottomsheet_select_course,null)
        bottomSheetDialogProgramme.setContentView(v3)

        v3.aviation.setOnClickListener {
            v3.aviationImg.visibility = View.VISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Aviation Transport"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.business.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.VISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Business Administration"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.chemical.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.VISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Chemical Technology"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.civil.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.VISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Civil Engineering"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.computer.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.VISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Computer Science/ Systems"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.clothing.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.VISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Clothing and Textile Technology"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.economics.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.VISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Economics"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.electrical.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.VISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Electrical Technology"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.electronics.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.VISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Electronics and Mobile Communication"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.mechanical.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.VISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Mechanical/Mechanics Engineering"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.entrepreneur.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.VISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Entrepreneurship and Management"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.environmental.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.VISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Environmental Engineering"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.material.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.VISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Material Science"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.medical.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.VISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Medical Engineering and Physics"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.telecommunications.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.VISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Telecommunications"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.medicine.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.VISIBLE
            v3.otherImg.visibility = View.INVISIBLE
            textProgramme.text = "Medicine"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        v3.other.setOnClickListener {
            v3.aviationImg.visibility = View.INVISIBLE
            v3.businessImg.visibility = View.INVISIBLE
            v3.chemicalImg.visibility = View.INVISIBLE
            v3.civilImg.visibility = View.INVISIBLE
            v3.computerImg.visibility = View.INVISIBLE
            v3.clothingImg.visibility = View.INVISIBLE
            v3.economicsImg.visibility = View.INVISIBLE
            v3.electricalImg.visibility = View.INVISIBLE
            v3.electronicsImg.visibility = View.INVISIBLE
            v3.mechanicalImg.visibility = View.INVISIBLE
            v3.entrepreneurImg.visibility = View.INVISIBLE
            v3.environmentalImg.visibility = View.INVISIBLE
            v3.materialImg.visibility = View.INVISIBLE
            v3.medicalImg.visibility = View.INVISIBLE
            v3.telecommunicationsImg.visibility = View.INVISIBLE
            v3.medicineImg.visibility = View.INVISIBLE
            v3.otherImg.visibility = View.VISIBLE
            textProgramme.text = "Other"
            bottomSheetDialogProgramme.dismiss()
            checkCondition()
        }

        university!!.setOnClickListener {
            bottomSheetDialogUniversity.show()
        }

        degree!!.setOnClickListener {
            bottomSheetDialogDegree.show()
        }

        programme!!.setOnClickListener {
            bottomSheetDialogProgramme.show()
        }

        btnDetails.setOnClickListener {
            btnDetails.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsexit))
            btnDetails?.visibility = View.GONE
            textDetailsSure?.visibility = View.VISIBLE
            textDetailsSure?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetails))
            btnDetailsNo?.visibility = View.VISIBLE
            btnDetailsNo?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsno))
            btnDetailsYes?.visibility = View.VISIBLE
            btnDetailsYes?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsyes))
            textDetailsCanNotChanged?.visibility = View.VISIBLE
            textDetailsCanNotChanged.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsentryup))
            university.isClickable = false
            degree.isClickable = false
            programme.isClickable = false
        }

        btnDetailsNo?.setOnClickListener {
            textDetailsSure?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsexitup))
            textDetailsSure?.visibility = View.GONE
            btnDetailsNo?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsnoexit))
            btnDetailsYes?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsyesexit))
            btnDetailsNo?.visibility = View.GONE
            btnDetailsYes?.visibility = View.GONE
            btnDetails?.visibility = View.VISIBLE
            btnDetails.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsentryup))
            textDetailsCanNotChanged.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsexit))
            textDetailsCanNotChanged.visibility = View.GONE
            university.isClickable = true
            degree.isClickable = true
            programme.isClickable = true

        }

        btnDetailsYes?.setOnClickListener {
            val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
            val userMap = HashMap<String, Any>()
            userMap["college"] =textUniversity.text.toString()
            userMap["degree"] = textDegree.text.toString()
            userMap["programme"] = textProgramme.text.toString()
            usersRef.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(userMap).addOnCompleteListener {
                if (it.isSuccessful){
                    SaveLanguage(this).setCollege(textDegree.text.toString())
                    startActivity(Intent(this,SuggestionsActivity::class.java))
                    finish()
                }
            }
        }
    }

    fun checkCondition(){
        if (textDegree.text == "Select your Degree" || textProgramme.text == "Select your Programme" || textUniversity.text == "Select your University")
        {
            btnDetails?.visibility = View.INVISIBLE
        }else{

            if (btnDetails?.visibility != View.VISIBLE){
                val anim = AnimationUtils.loadAnimation(this,R.anim.animdetails)
                btnDetails?.startAnimation(anim)
                btnDetails?.visibility = View.VISIBLE
            }else{
                btnDetails?.visibility = View.VISIBLE
            }

        }
    }

}