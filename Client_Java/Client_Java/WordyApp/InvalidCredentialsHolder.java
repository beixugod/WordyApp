package Client_Java.WordyApp;

/**
* WordyApp/InvalidCredentialsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from WordyApp.idl
* Sunday, May 28, 2023 11:06:31 PM SGT
*/

public final class InvalidCredentialsHolder implements org.omg.CORBA.portable.Streamable
{
  public InvalidCredentials value = null;

  public InvalidCredentialsHolder ()
  {
  }

  public InvalidCredentialsHolder (InvalidCredentials initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = InvalidCredentialsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    InvalidCredentialsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return InvalidCredentialsHelper.type ();
  }

}
