//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.9
//
// <auto-generated>
//
// Generated from file `Dynamic.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Dynamic;

public enum LongitudeType implements java.io.Serializable
{
    N(0),
    S(1);

    public int value()
    {
        return _value;
    }

    public static LongitudeType valueOf(int v)
    {
        switch(v)
        {
        case 0:
            return N;
        case 1:
            return S;
        }
        return null;
    }

    private LongitudeType(int v)
    {
        _value = v;
    }

    public void ice_write(com.zeroc.Ice.OutputStream ostr)
    {
        ostr.writeEnum(_value, 1);
    }

    public static void ice_write(com.zeroc.Ice.OutputStream ostr, LongitudeType v)
    {
        if(v == null)
        {
            ostr.writeEnum(Dynamic.LongitudeType.N.value(), 1);
        }
        else
        {
            ostr.writeEnum(v.value(), 1);
        }
    }

    public static LongitudeType ice_read(com.zeroc.Ice.InputStream istr)
    {
        int v = istr.readEnum(1);
        return validate(v);
    }

    public static void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<LongitudeType> v)
    {
        if(v != null && v.isPresent())
        {
            ice_write(ostr, tag, v.get());
        }
    }

    public static void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, LongitudeType v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.Size))
        {
            ice_write(ostr, v);
        }
    }

    public static java.util.Optional<LongitudeType> ice_read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.Size))
        {
            return java.util.Optional.of(ice_read(istr));
        }
        else
        {
            return java.util.Optional.empty();
        }
    }

    private static LongitudeType validate(int v)
    {
        final LongitudeType e = valueOf(v);
        if(e == null)
        {
            throw new com.zeroc.Ice.MarshalException("enumerator value " + v + " is out of range");
        }
        return e;
    }

    private final int _value;
}
