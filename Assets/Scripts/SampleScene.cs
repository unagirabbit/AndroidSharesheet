using UnityEngine;
using UnityEngine.UI;

public class SampleScene : MonoBehaviour
{
    [SerializeField] private Button _captureBtn;
    [SerializeField] private Button _shareBtn;
    private static readonly string CAPTURE_FILE_NAME = "capture.png";

    public void Start()
    {
        _captureBtn.onClick.AddListener(() => ScreenCapture.CaptureScreenshot(CAPTURE_FILE_NAME));
        _shareBtn.onClick.AddListener(() => Share());
    }

    private void Share()
    {
        using(var androidCls = new AndroidJavaClass("com.example.mylibrary.SocialShare"))
        {
            var title = "共有先を選択してください";
            var message = "ほげほげアプリからの投稿です";
            var shareFilePath = System.IO.Path.Combine(Application.persistentDataPath, CAPTURE_FILE_NAME);
            androidCls.CallStatic("send", title, message, "image/png", shareFilePath, "twitter");
        }
    }
}
