using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
/// <summary>
/// toDo
///known bugs: wenn die alte positionierung noch nicht abgeschlossen ist wird eine neue positionierung nicht gehen
/// 
/// </summary>


public class CameraReposition : MonoBehaviour
{

    #region Public properties
    [Header("Player and Camera Object")]
    public GameObject player;
    public GameObject cameraToMove;

    [Header("Smooth Rotation and Positioning")]
    public bool smoothPositioning = false;
    public float repositionSpeed = 1;
    [Space]
    public bool smoothRotation = false;
    public float rotationSpeed = 1;

    [Header("Costom camera Position")]
    public Vector3 targetPosition;

    [Header("Object bound camera Reposition")]
    [Tooltip("Enable for Object bound repositioning.")]
    public bool objectReposition = false;
    public GameObject targetObject;
    [Tooltip("Camera to Object Offset")]
    public Vector3 cameraOffset;

    [Header("Camera Rotation")]
    public Vector3 targetRotationAngle;
    #endregion

    #region private members
    private Vector3 targetObjectPosition;
    private Vector3 rotationAngle;
    private bool moveEvent = false;
    private bool positionReached = false;
    private bool rotationReached = false;
    private Quaternion targetRotationQuaternion;
    private float timeCount = 0.0f;
    private float konstante = 1.01f;


    #endregion

    #region unity override methods
    void Start()
    {
        if (targetObject == null)
        {
            targetObject = player;
        }
    }
    void LateUpdate()
    {
        if (positionReached && rotationReached) moveEvent = false;
        if (moveEvent)
        {
            MoveCamera();
        }
    }

    void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.name == player.name)
        {
            if ((targetObject == player || targetObject == null) && objectReposition)
            {
                Debug.Log("Das Zielobjekt der Kamerapositionierung von Trigger: " + gameObject.name + " ist nicht gesetzt!!");
            }
            rotationReached = false;
            positionReached = false;
            moveEvent = true;
        }
    }
    #endregion

    #region private methods
    void MoveCamera()
    {
        SetTargetQuaternion();

        targetObjectPosition = targetObject.gameObject.transform.position + cameraOffset;

        RotationChange();

        if (!positionReached)
        {
            if (objectReposition)
            {
                PositionChange(targetObjectPosition);
            }
            else
            {
                PositionChange(targetPosition);
            }
        }
    }

    private void PositionChange(Vector3 position)
    {
        if (smoothPositioning)
        {
            cameraToMove.gameObject.transform.position = Vector3.Lerp(cameraToMove.gameObject.transform.position, position, Time.deltaTime * repositionSpeed);
        }
        else
        {
            cameraToMove.gameObject.transform.position = position;
        }

        if (Math.Abs(cameraToMove.gameObject.transform.position.x * konstante) >= Math.Abs(position.x) && Math.Abs(cameraToMove.gameObject.transform.position.y * konstante) >= Math.Abs(position.y) && Math.Abs(cameraToMove.gameObject.transform.position.z * konstante) >= Math.Abs(position.z))
        {
            positionReached = true;
        }
    }

    void RotationChange()
    {
        if (!rotationReached)
        {
            if (smoothRotation)
            {
                cameraToMove.transform.rotation = Quaternion.Lerp(cameraToMove.transform.rotation, targetRotationQuaternion, rotationSpeed / 100);

                if (Math.Abs(cameraToMove.gameObject.transform.rotation.x * konstante) >= Math.Abs(targetRotationQuaternion.x) && Math.Abs(cameraToMove.gameObject.transform.rotation.y * konstante) >= Math.Abs(targetRotationQuaternion.y) && Math.Abs(cameraToMove.gameObject.transform.rotation.z * konstante) >= Math.Abs(targetRotationQuaternion.z))
                {
                    rotationReached = true;
                }
            }
            else
            {
                cameraToMove.transform.rotation = targetRotationQuaternion;
                rotationReached = true;
            }
        }
    }

    private void SetTargetQuaternion()
    {
        targetRotationQuaternion = Quaternion.Euler(targetRotationAngle.x, targetRotationAngle.y, targetRotationAngle.z);
    }
    #endregion
}
