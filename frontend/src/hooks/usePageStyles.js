import { useEffect } from "react";

export default function usePageStyles(stylesheetUrl) {
  useEffect(() => {
    const stylesheet = document.createElement("link");
    stylesheet.rel = "stylesheet";
    stylesheet.href = stylesheetUrl;
    document.head.appendChild(stylesheet);

    return () => {
      stylesheet.remove();
    };
  }, [stylesheetUrl]);
}
