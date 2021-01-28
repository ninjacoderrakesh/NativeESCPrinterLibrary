using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Native.Esc.Printer.Library.RNNativeEscPrinterLibrary
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNNativeEscPrinterLibraryModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNNativeEscPrinterLibraryModule"/>.
        /// </summary>
        internal RNNativeEscPrinterLibraryModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNNativeEscPrinterLibrary";
            }
        }
    }
}
