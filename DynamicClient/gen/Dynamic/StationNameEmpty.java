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

public class StationNameEmpty extends com.zeroc.Ice.UserException
{
    public StationNameEmpty()
    {
    }

    public StationNameEmpty(Throwable cause)
    {
        super(cause);
    }

    public String ice_id()
    {
        return "::Dynamic::StationNameEmpty";
    }

    /** @hidden */
    @Override
    protected void _writeImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice("::Dynamic::StationNameEmpty", -1, true);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _readImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        istr_.endSlice();
    }

    /** @hidden */
    public static final long serialVersionUID = 2489360642754983098L;
}