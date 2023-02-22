package com.nancone.criminalintent.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.hardware.camera2.CameraAccessException
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.nancone.criminalintent.R
import com.nancone.criminalintent.getScaledBitmap
import com.nancone.criminalintent.model.Crime
import com.nancone.criminalintent.viewmodel.CrimeListViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
const val KEY_DATE = "crime_date"
private val DATE_FORMAT = "yyyy년 M월 d일 H시 m분, E요일"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    // CrimeListViewModel 참조
    // ViewModel이 Fragment와 같이 사용되면 ViewModel의 생명주기는 Fragment의 생명주기를 따라감
    private val crimeListViewModel: CrimeListViewModel by lazy {
        // ViewModelProvider(this): 현재의 CrimeListFragment 인스턴스와 연관된 ViewModelProvider 인스턴스를 생성하고 반환
        // get(CrimeListViewModel::class.java): CrimeListViewModel 인스턴스 반환
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    /**
     * Fragment 에서는 View 를 onCreate 에서 inflate(객체화 시켜 메모리에 올려놓는 것(layout 을 원하는 것으로 변경 가능)) 하지 않음.
     * onCreateView 에서 생성하고 구성함
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeListViewModel.loadCrime(crimeId) // id setting
//        Log.d(TAG, "onCreate: args bundle crime ID: $crimeId")
    }

    /**
     * Fragment View 의 layout 을 inflate 한 후 inflate 된 view 를 호스팅 액티비티에 반환해야 함.
     * Bundle 은 저장된 상태 데이터 가지며 View 를 재생성하는데 사용
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * @resource : inflate 함수에 R.layout.fragment_crime 을 인자로 전달하여 fragment view 를 명시적으로 inflate 함.
         * @viewGroup : 위젯들을 올바르게 구성하는 데 필요한 view 의 부모
         * @attachRoot: inflate 된 view 를 이 view 의 부모에게 즉시 추가할 것인지 LayoutInflater 에 알려줌.
         *              여기서는 현재 false : why? 이 fragment 의 view 는 activity 의 container view 에 호스팅되기 때문.
         *              즉, 이 fragment 의 view 는 inflate 되는 즉시 부모 view 에 추가할 필요가 없으며,
         *              Activity 가 나중이 이 view 를 추가한다.
         */
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
//        dateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button

        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeListViewModel.getPhotoCrime(crime)
                    // getUriForFile() 을 통해 로컬 파일 시스템의 파일 경로를 카메라 앱에서 알 수 있는 Uri로 변환한다
                    photoUri = FileProvider.getUriForFile(requireActivity(),
                        "com.nanacone.criminalintent.fileprovider",
                        photoFile)
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
//        solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // 애니메이션 생략
        }

        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }

    // ImageView에 Bitmap을 로드하기 위한 함수
    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // 비워둠
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // 비워둠.
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }

        dateButton.setOnClickListener {
//            DatePickerFragment().apply {
//                // FragmentTransaction 을 쓰면 트랜잭션 생성 후 커밋해줘야하는데
//                // FragmentManager 는 자동으로 트랜잭션을 만들고 커밋해줌
//                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
//            }

            // setTargetFragment is deprecated
            parentFragmentManager.setFragmentResultListener(KEY_DATE, viewLifecycleOwner,
                FragmentResultListener { key, bundle ->
                    if (key == KEY_DATE) {
                        onDateSelected(bundle.getSerializable(KEY_DATE) as Date)
                    }
                })

            DatePickerFragment.newInstance(crime.date).apply {
                // FragmentTransaction 을 쓰면 트랜잭션 생성 후 커밋해줘야하는데
                // FragmentManager 는 자동으로 트랜잭션을 만들고 커밋해줌
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        // 보고서 전송
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport()) // 데이터
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject)
                ) // 제목: 범죄 보고서
            }.also { intent ->
//                startActivity(intent) // 사용자가 어떤 앱을 항상 허용으로 했다면 다음부터는 chooser 없이 해당 앱으로 열림

                val chooserIntent =
                    Intent.createChooser(
                        intent,
                        getString(R.string.send_report)
                    ) // 사용자가 어떤 앱을 항상 허용했더라도 처리할 수 있는 activity가 하나 이상이면 항상 chooser 창을 열어줌
                startActivity(chooserIntent)
            }
        }

        // 용의자 전송
        suspectButton.apply {
            val pickContentIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                suspectResult.launch(pickContentIntent)
            }

            // 연락처 앱이 없으면 버튼 비활성화
            val packageManager: PackageManager = requireActivity().packageManager
            val resolveActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContentIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null)
                isEnabled = false
        }

        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라 앱을 시작시키고 찍은 사진을 받을 수 있게 해줌
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) { // 앱이 장치에 없거나, 사진을 저장할 위치가 없으면 카메라 버튼 비활성화
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    photoUri
                ) // EXTRA_OUTPUT : 요청된 이미지나 비디오를 저장하는 때 쓰이는 content resolver Uri의 이름

                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(
                        captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )

                for (cameraActivity in cameraActivities) {
                    // photoUri가 가리키는 위치에 실제로 사진 파일을 쓰려면 카메라 앱 퍼미션이 필요.
                    // 따라서 cameraImage 인텐트를 처리할 수 있는 모든 Activity에 Intent.FLAG_GRANT_WRITE_URI_PERMISSION을 부여 함.(Manifest에 grantUriPermissions 속성을 추가했기 때문에 permission 부여 가능)
                    // 이러면 해당 activity(=cameraImage 인텐트를 처리할 수 있는 cameraActivity) 들이 Uri에 쓸 수 있는 Permission을 갖는다.
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                cameraCaptureResult.launch(captureImage)

            }
        }

        photoView.setOnClickListener {
            val ft = parentFragmentManager.beginTransaction()
            val prev = parentFragmentManager.findFragmentByTag("dialogImg")
            prev?.also { ft.remove(prev) }

            CrimeImgDialogFragment.newInstance(
                bitmap =
                if (photoFile.exists()) {
                    getScaledBitmap(photoFile.path, requireActivity())
                } else {
                    null
                }
            ).show(ft, "dialogImg")
//            (if(photoFile.exists())
//                getScaledBitmap(photoFile.path, requireActivity())
//            else
//                null)?.let { it1 ->
//                CrimeImgDialogFragment.newInstance(bitmap =
//                it1
//                ).show(ft, "dialogImg")
        }
    }

    // startActivityResult is deprecated 1
    // 1:1로 매칭됨.
    private val suspectResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            when {
                result.resultCode != RESULT_OK -> return@registerForActivityResult // RESULT_OK 가 아니면 return

                result.resultCode == RESULT_OK && result.data != null -> {
                    val contactUri: Uri = result.data?.data ?: return@registerForActivityResult

                    // 쿼리에서 값으로 반환할 필드를 지정한다
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                    // 쿼리를 수행한다. contactUri는 콘턴츠 제공자의 테이블을 나타낸다.
                    val cursor = context?.contentResolver?.query(contactUri, queryFields, null, null, null)
                    cursor?.use {
                        // 쿼리 결과 테이터가 있는지 확딘한다.
                        if(it.count == 0) {
                            return@registerForActivityResult
                        }

                        // 첫번째 데이터 행의 첫번째 열의 값을 가져온다.
                        // 이 값이 용의자의 이름이다
                        it.moveToFirst()
                        val suspect = it.getString(0)
                        crime.suspect = suspect
                        crimeListViewModel.saveCrime(crime)
                        suspectButton.text = suspect
                    }

                }
            }
        }
    private val cameraCaptureResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            when {
                result.resultCode != RESULT_OK -> return@registerForActivityResult // RESULT_OK 가 아니면 return
                result.resultCode == RESULT_OK -> {
                    // 정상적으로 카메라 앱 사용하여 파일을 저장한 후
                    requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Uri에 파일을 쓸 수 있는 권한 취소
                    updatePhotoView()
                }
//                result.resultCode == RESULT_OK && result.data != null -> {
//                    val contactUri: Uri = result.data?.data ?: return@registerForActivityResult
//
//
//                }
            }
        }

    override fun onDateSelected(date: Date) {
//        TimePickerFragment().apply {
//            show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
//        }
        TimePickerFragment.newInstance(date).apply {
            show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
        }
        parentFragmentManager.setFragmentResultListener(KEY_DATE, viewLifecycleOwner,
            FragmentResultListener { key, bundle ->
                if(key == KEY_DATE) {
                    val date = bundle.getSerializable(KEY_DATE) as Date
//                    date.time = bundle.getSerializable(ARG_TIME) as Time
                    onTimeSelected(date)
                }})
    }

    override fun onTimeSelected(date: Date) {
        crime.date = date
        updateUI()
    }
    override fun onStop() {
        super.onStop()
        crimeListViewModel.saveCrime(crime) // 저장
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Uri에 파일을 쓸 수 있는 권한 취소
    }
    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString =
            if(crime.isSolved)
                getString(R.string.crime_report_solved)
            else
                getString(R.string.crime_report_unsolved)

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect =
            if (crime.suspect.isBlank())
                getString(R.string.crime_report_no_suspect)
            else
                getString(R.string.crime_report_suspect, crime.suspect)

        return getString(R.string.crime_report,
            crime.title, dateString, solvedString, suspect)
    }
}